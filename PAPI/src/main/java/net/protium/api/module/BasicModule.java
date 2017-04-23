/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.module;

import net.protium.api.annotations.OnDisable;
import net.protium.api.annotations.OnEnable;
import net.protium.api.http.Request;
import net.protium.api.http.Response;

public abstract class BasicModule extends AbstractModule {
	@OnEnable
	public void onEnable( ) {
	}

	@Override
	public Response onRequest(Request request) {
		return null;
	}

	@OnDisable
	public void onDisable( ) {
	}
}
