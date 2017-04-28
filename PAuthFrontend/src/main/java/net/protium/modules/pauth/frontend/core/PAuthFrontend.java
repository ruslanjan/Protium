/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.frontend.core;
/*
In net.protium.modules.pauth.frontend
From temporary-protium
*/

import net.protium.api.annotations.OnEnable;
import net.protium.api.http.Request;
import net.protium.api.http.Response;
import net.protium.api.module.AbstractModule;
import net.protium.api.module.IModule;
import net.protium.modules.pauth.backend.http.PAResponse;
import net.protium.modules.pauth.frontend.util.C;
import net.protium.modules.pauth.frontend.util.Storage;
import net.protium.modules.pauth.frontend.view.Template;

public class PAuthFrontend implements IModule {

	@OnEnable
	public void init( ) {
		Storage.manager = AbstractModule.getModuleManager();
	}

	@Override
	public Response onRequest(Request request) {
		if (request.getAction().equals("static")) {
			String resId = (String) request.getOptions().get("resource");

			Template template = new Template(resId);

			template
				.processRegex(C.STATIC_REGEX, C.STATIC_LINK)
				.exec();

			PAResponse response = new PAResponse();

			response.setResponse(template.toString());
			response.setContentType(template.toMimeType());

			return response;
		}

		return null;
	}


}
