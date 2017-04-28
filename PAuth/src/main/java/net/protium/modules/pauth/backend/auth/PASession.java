/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.backend.auth;
/*
In net.protium.modules.pauth.auth
From temporary-protium
*/

public class PASession {

	String rawSession, code;
	Integer userId;

	public PASession(String session) {
		if (session == null) {
			code = "";
			userId = -1;
			return;
		}

		session = session.trim();
		String[] sessData = session.split(":", 2);

		if (sessData.length < 2) {
			code = "";
			userId = -1;
			return;
		}

		userId = Integer.valueOf(sessData[0]);
		code = sessData[1];

		rawSession = session;
	}

	public String getRawSession( ) {
		return rawSession;
	}

	public String getCode( ) {
		return code;
	}

	public Integer getUserId( ) {
		return userId;
	}


}
