/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.database.orm;

import groovy.sql.GroovyRowResult;
import net.protium.modules.pauth.auth.PARank;


@SuppressWarnings("JpaDataSourceORMInspection")


public class PAUser {

	//region Type definition
	private Integer id;
	private String login;
	private String email;
	private String password;
	private String firstName = "";
	private String middleName = "";
	private String lastName = "";
	private PARank rank;

	public PAUser(String login, String email, String password, String firstName, String middleName, String lastName, PARank rank) {
		this.login = login;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.rank = rank;
	}

	public PAUser( ) {
	}
	//endregion

	public PAUser(GroovyRowResult rowResult) {
		id = (Integer) rowResult.get("user_id");
		login = (String) rowResult.get("login");
		email = (String) rowResult.get("email");
		password = (String) rowResult.get("password");
		firstName = (String) rowResult.get("first_name");
		middleName = (String) rowResult.get("middle_name");
		lastName = (String) rowResult.get("last_name");
		try {
			rank = PARank.valueOf((String) rowResult.get("perm_group"));
		} catch (NullPointerException ignored) {
			rank = PARank.USR;
		}
	}

	@Override
	public String toString( ) {
		return "USER: { " +
			" id = " + id +
			"; login = " + login +
			"; email = " + email +
			"; password = " + password +
			"; firstName = " + firstName +
			"; middleName = " + middleName +
			"; lastName = " + lastName +
			"; rank = " + rank + " }";
	}

	//region Getters and Setters
	public Integer getId( ) {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin( ) {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail( ) {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword( ) {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName( ) {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName( ) {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName( ) {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public PARank getRank( ) {
		return rank;
	}

	public void setRank(PARank rank) {
		this.rank = rank;
	}

	//endregion
}
