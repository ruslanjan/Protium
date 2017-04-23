/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.database

import net.protium.modules.pauth.auth.PASession
import net.protium.modules.pauth.database.orm.PAUser
import net.protium.modules.pauth.utils.C
import net.protium.modules.pauth.utils.Storage
import org.mindrot.jbcrypt.BCrypt

import java.sql.SQLException

/*
In net.protium.modules.pauth.database
From temporary-protium
*/

class UserManager {

	private static final String TABLE_NAME = 'pauth_users'

	private static
	final String SQL_GET_BY_ID = "SELECT * FROM ${TABLE_NAME} WHERE user_id=?"

	private static
	final String SQL_GET_BY_AUTH_OR_EMAIL = "SELECT * FROM ${TABLE_NAME} WHERE login=:login OR email=:login"

	private static
	final String SQL_GET_BY_LOGIN_OR_EMAIL = "SELECT * FROM ${TABLE_NAME} WHERE login=:login OR email=:email"

	private static
	final String SQL_GET_BY_LOGIN = "SELECT * FROM ${TABLE_NAME} WHERE login=:login"

	private static
	final String SQL_DROP_BY_ID = "DELETE FROM ${TABLE_NAME} WHERE user_id=?"

	private static
	final String SQL_NEW = "INSERT INTO ${TABLE_NAME} " +
			"(login, email, password, first_name, middle_name, last_name, perm_group)" +
			"VALUES (:login, :email, :password, :first_name, :middle_name, :last_name, :perm_group)"

	private static
	final String SQL_UPDATE_BY_ID = "UPDATE  ${TABLE_NAME} SET" +
			"(login=:login, email=:email, password=:password, first_name=:first_name," +
			"middle_name=:middle_name, last_name=:last_name, perm_group=:perm_group)" +
			"WHERE id=:id"

	SqlWrapper wrapper

	UserManager() {
		wrapper = new SqlWrapper()
//		createTable()
	}

	void createTable() {
		try {
			String tableDDL = Storage.readFile("ddl/pauth_users.sql")
			wrapper.connector.execute(tableDDL)
		} catch (SQLException ignored) {
		}
	}

	PAUser getById(Integer id) {
		def result = wrapper.connector.rows(SQL_GET_BY_ID, [id])
		if (result.size() < 1)
			return null
		new PAUser(result.get(0))
	}

	PAUser getByUnique(String login, String email) {
		def result = wrapper.connector.rows([
				login: login,
				email: email
		], SQL_GET_BY_LOGIN_OR_EMAIL)

		if (result.size() < 1)
			return null

		new PAUser(result.get(0))
	}

	PAUser newUser(PAUser user) {

		Map params = [
				login      : user.login,
				email      : user.email,
				password   : user.password,
				first_name : user.firstName,
				middle_name: user.middleName,
				last_name  : user.lastName,
				perm_group : user.rank
		]

		wrapper.connector.execute(params, SQL_NEW)

		user
	}

	PAUser alterUser(PAUser user) {

		Map params = [
				id         : user.id,
				login      : user.login,
				email      : user.email,
				password   : user.password,
				first_name : user.firstName,
				middle_name: user.middleName,
				last_name  : user.lastName,
				perm_group : user.rank
		]

		wrapper.connector.execute(params, SQL_UPDATE_BY_ID)

		user
	}


	void removeUser(Integer id) {
		wrapper.connector.execute(SQL_DROP_BY_ID, [id])
	}

	Integer getIdByAuth(String login, String password) {

		login = login.trim()
		password = password.trim()

		def result = wrapper.connector.rows([
				login: login
		], SQL_GET_BY_LOGIN)

		if (result.size() < 1)
			return null

		PAUser user = new PAUser(result.get(0))

		if (BCrypt.checkpw(password, user.password))
			return user.id

		null
	}

	Integer getIdByAuthOrEmail(String login, String password) {

		login = login.trim()
		password = password.trim()

		def result = wrapper.connector.rows([
				login: login
		], SQL_GET_BY_LOGIN_OR_EMAIL)

		if (result.size() < 1)
			return null

		PAUser user = new PAUser(result.get(0))

		if (BCrypt.checkpw(password, user.password))
			return user.id

		null
	}

	boolean checkPassword(Integer id, String password) {
		def result = wrapper.connector.rows(SQL_GET_BY_ID, [id])

		if (result.size() < 1)
			return true

		PAUser user = new PAUser(result.get(0))

		if (BCrypt.checkpw(password, user.password))
			return true

		false
	}

	String newSession(Integer id) {
		PAUser user = getById(id)

		if (user == null) {
			throw new Exception()
		}

		String userCode = user.login + ":" + user.password

		return user.id + ":" +
				BCrypt.hashpw(userCode, BCrypt.gensalt(C.BCRYPT_ROUNDS))
	}

	boolean verifySession(PASession session) {
		PAUser user = getById(session.userId)

		if (user == null) {
			return false
		}

		String userCode = user.login + ":" + user.password

		return BCrypt.checkpw(userCode, session.code)
	}

	void commit() {

	}

	void close() {

	}
}
