/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.module;

import net.protium.api.agents.ModuleManager;
import net.protium.api.events.Request;
import net.protium.api.events.Response;

public interface IModule {
    Response onRequest(Request request);
}
