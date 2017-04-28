/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.backend.utils

import net.protium.api.agents.ModuleManager
/*
In net.protium.modules.pauth.storage
From temporary-protium
*/

class Storage {

	static public ModuleManager manager

	static String readFile(String name) {
		URL url = manager.getModuleResourceURL(C.MODULE_NAME, name)

		url.text
	}

}
