/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pte;
/*
In net.protium.modules.pte
From temporary-protium
*/

import net.protium.api.agents.Config;
import net.protium.api.annotations.OnDisable;
import net.protium.api.annotations.OnEnable;
import net.protium.api.exceptions.FileReadException;
import net.protium.api.exceptions.NotFoundException;
import net.protium.api.http.HTTPResponse;
import net.protium.api.http.Request;
import net.protium.api.http.Response;
import net.protium.api.module.AbstractModule;
import net.protium.api.module.IModule;
import net.protium.modules.pte.basic.PBasicTemplatizer;

import java.io.IOException;

public class Responder implements IModule {

	static public final String MODULE_NAME = "LibPTE/Basic";
	static public final String CONFIG_FILE = MODULE_NAME + "/libpte";
	static public final String RESPONSE_NOTIFY = "This is a library, not callable!";

	@OnEnable
	@OnDisable
	public void verifyConfigs( ) {
		Config config;
		try {
			if (!Config.configExists(CONFIG_FILE)) {
				Config.createConfig(CONFIG_FILE, "{}");
			}
			config = new Config(CONFIG_FILE);
		} catch (IOException | FileReadException e) {
			try {
				AbstractModule.getModuleManager().setModuleViewStatus(MODULE_NAME, "CFG_ERR");
			} catch (NotFoundException ignored) {
			}
			return;
		}

		PBasicTemplatizer.CACHE_SOFT_LIMIT = config.checkPath("basic.softCacheLimit") ?
			config.getInteger("basic.softCacheLimit") :
			PBasicTemplatizer.CACHE_SOFT_LIMIT;

		PBasicTemplatizer.CACHE_HARD_LIMIT = config.checkPath("basic.hardCacheLimit") ?
			config.getInteger("basic.softCacheLimit") :
			PBasicTemplatizer.CACHE_HARD_LIMIT;

		config
			.set("basic.hardCacheLimit", PBasicTemplatizer.CACHE_HARD_LIMIT)
			.set("basic.softCacheLimit", PBasicTemplatizer.CACHE_SOFT_LIMIT)
			.commit();
	}

	@Override
	public Response onRequest(Request request) {
		HTTPResponse response = new HTTPResponse();
		response.setResponse(RESPONSE_NOTIFY);
		return response;
	}
}
