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

	public boolean verifyLogin(String login, String password) {
		User user;
		try {
			//noinspection UnusedAssignment
			user = (User) entityManager
				.createQuery("select User.password from User where login=?")
				.setParameter(0, login)
				.getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
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
