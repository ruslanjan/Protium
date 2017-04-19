/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
In PACKAGE_NAME
From temporary-protium
*/

import net.protium.modules.pauth.database.UserManager;

import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] argv) {

		Map < String, String > props = new HashMap <>();

		props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres");
		props.put("hibernate.connection.user", "postgres");
		props.put("hibernate.connection.password", "9827");

		UserManager userManager = new UserManager(props);

		System.out.println(userManager.getByLogin("foo"));

		userManager.close();

		System.out.println(userManager.entityManager);
	}
}
