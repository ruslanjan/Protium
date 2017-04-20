/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.storage

import net.protium.api.agents.ModuleManager
import net.protium.modules.pauth.utils.C

/*
In net.protium.modules.pauth.storage
From temporary-protium
*/

class Resource {

	static public ModuleManager manager

	static String readFile(String name) {
		URL url = manager.getModuleResourceURL(C.MODULE_NAME, name)
		url.text
	}

}
