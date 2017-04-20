/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.database.orm;
/*
In net.protium.modules.pauth.database.orm
From temporary-protium
*/

import net.protium.modules.pauth.oauth2.OAuthType;

import javax.persistence.*;


@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "pauth_users")
public class User {

	//region DisplayType definition
	@Id
	@Column(name = "user_id", nullable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "login", nullable = false, unique = true)
	private String login;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "first_name")
	private String firstName = "";

	@Column(name = "middle_name")
	private String middleName = "";

	@Column(name = "last_name")
	private String lastName = "";

	@Column(name = "oauth_bind")
	private String oauthBind;

	@Column(name = "oauth_type")
	private OAuthType oauthType;
	//endregion

	public User(String login, String email, String password, String firstName, String middleName, String lastName, String googleBind) {
		this.login = login;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.oauthBind = googleBind;
	}

	public User( ) {
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

	public String getOauthBind( ) {
		return oauthBind;
	}

	public void setOauthBind(String googleBind) {
		this.oauthBind = googleBind;
	}

	public OAuthType getOauthType( ) {
		return oauthType;
	}

	public void setOauthType(OAuthType oauthType) {
		this.oauthType = oauthType;
	}

	//endregion
}
