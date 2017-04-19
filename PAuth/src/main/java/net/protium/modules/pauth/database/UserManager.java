/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.database;
/*
In net.protium.modules.pauth.database
From temporary-protium
*/

import net.protium.modules.pauth.database.orm.User;
import net.protium.modules.pauth.utils.C;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.Map;

public class UserManager {
	public EntityManager entityManager;


	public UserManager(Map props) {
		entityManager = Persistence
			.createEntityManagerFactory(C.DB_UNIT, props)
			.createEntityManager();

		start();
	}

	public User getByID(Integer userId) {
		return entityManager.find(User.class, userId);
	}

	public User getByLogin(String login) {
		try {
			return (User) entityManager
				.createQuery("from User where login=?")
				.setParameter(0, login)
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Integer verifyLogin(String login, String password) {
		User user;

		try {
			user = (User) entityManager
				.createQuery("select User.password, User.id from User where login=?")
				.setParameter(0, login)
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

		if (BCrypt.checkpw(password, user.getPassword())) {
			return user.getId();
		}

		return null;
	}

	public String newSession(Integer id) {
		User user = getByID(id);
		String session = user.getLogin() + ":" + user.getPassword();

		session = user.getId() + ":" + BCrypt.hashpw(session, BCrypt.gensalt());

		return String.valueOf(session);
	}

	public boolean verifySession(String session) {
		String[] divided = session.split(":");
		if (divided.length < 2)
			return false;

		Integer userId = Integer.valueOf(divided[0]);
		User user = getByID(userId);
		String sessionHash = divided[1];

		return BCrypt.checkpw(user.getLogin() + ":" + user.getPassword(), sessionHash);
	}

	public User newUser(String login, String email, String password, String firstName, String middleName, String lastName, String googleBinding) {
		User user = new User(login, email, password, firstName, middleName, lastName, googleBinding);
		entityManager.persist(user);

		return user;
	}

	public void start( ) {
		entityManager.getTransaction().begin();
	}

	public void commit( ) {
		entityManager.getTransaction().commit();
	}

	public void revert( ) {
		entityManager.getTransaction().rollback();
	}

	public void close( ) {
		commit();
		entityManager.close();
		entityManager = null;
	}
}
