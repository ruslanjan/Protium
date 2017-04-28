/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
In PACKAGE_NAME
From temporary-protium
*/

import net.protium.modules.pauth.backend.database.UserManager;

public class Test {

	public static void main(String[] argv) {
		UserManager userManager = new UserManager();
		String session = userManager.newSession(1);
		System.out.println(session);
	}
}
