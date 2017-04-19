/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.module;

import net.protium.api.agents.ModuleManager;
import net.protium.api.annotations.OnDisable;
import net.protium.api.annotations.OnEnable;
import net.protium.api.events.Request;
import net.protium.api.events.Response;

public abstract class AbstractModule implements IModule {

	public static ModuleManager moduleManager = null;

	@OnEnable
	public abstract void onEnable( );

	@Override
	public abstract Response onRequest(Request request);

	@OnDisable
	public abstract void onDisable( );
}
