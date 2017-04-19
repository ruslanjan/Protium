/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.module;

import net.protium.api.events.Request;
import net.protium.api.events.Response;

public abstract class BasicModule extends AbstractModule {
	@Override
	public void onEnable( ) {
	}

	@Override
	public Response onRequest(Request request) {
		return null;
	}

	@Override
	public void onDisable( ) {
	}
}
