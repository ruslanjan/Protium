/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.module;

import net.protium.Protium;
import net.protium.api.agents.ModuleManager;
import net.protium.api.events.Request;
import net.protium.api.events.Response;

public abstract class AbstractModule implements IModule {

    public static ModuleManager moduleManager = null;

    @Override
    public abstract void onEnable();

    @Override
    public abstract Response onRequest(Request request);

    @Override
    public abstract void onDisable();
}
