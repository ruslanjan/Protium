/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http;

import net.protium.api.exceptions.NotFoundException;
import net.protium.api.http.Request;
import net.protium.api.http.Response;
import net.protium.core.modules.management.Manager;

abstract public class AbstractRouter {

	protected Manager manager;

	protected Response _perform(String moduleName, Request request) throws NotFoundException {
		return manager.getModule(moduleName).onRequest(request);
	}

}
