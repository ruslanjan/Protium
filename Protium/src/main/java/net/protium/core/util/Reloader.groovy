/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.util

import net.protium.Protium
import net.protium.api.utils.Constant
import net.protium.api.utils.Functions
import net.protium.core.http.Router
import net.protium.core.modules.management.Manager

import java.util.logging.FileHandler
import java.util.logging.Logger

/*
In net.protium.core.util
From temporary-protium
*/

class Reloader implements Runnable {

	Router router
	Manager manager
	Logger logger = Logger.getGlobal()

	static public long ROUTER_RELOAD_INTERVAL
	static public long MANAGER_RELOAD_INTERVAL
	static public long ROUTER_LAST_RELOAD = 10 * 1000
	static public long MANAGER_LAST_RELOAD = 0

	Reloader(Router router, Manager manager) {
		logger.addHandler(
				(new FileHandler(
						Functions.createFile(Constant.LOG_DIR, Protium.class.getName(), Constant.LOG_EXT)
				)))
		this.router = router
		this.manager = manager
	}

	@Override
	void run() {
		//noinspection GroovyInfiniteLoopStatement
		while (true) {
			long fromRouterReload = System.currentTimeMillis() - ROUTER_LAST_RELOAD

			if (fromRouterReload >= ROUTER_RELOAD_INTERVAL && ROUTER_RELOAD_INTERVAL > 0) {
				router.reload()
				ROUTER_LAST_RELOAD = System.currentTimeMillis()
				logger.info("Router reloaded!")
			}

			long fromManagerReload = System.currentTimeMillis() - MANAGER_LAST_RELOAD

			if (fromManagerReload >= MANAGER_RELOAD_INTERVAL && MANAGER_RELOAD_INTERVAL > 0) {
				manager.reloadModules()
				MANAGER_LAST_RELOAD = System.currentTimeMillis()
				logger.info("Manager reloaded!")
			}

		}
	}
}
