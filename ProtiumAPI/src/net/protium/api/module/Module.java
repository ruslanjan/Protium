/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.module;

import net.protium.api.events.Request;
import net.protium.api.events.Response;

public abstract class Module {
    public abstract void onEnable();

    public abstract Response onRequest(Request request);

    public abstract void onDisable();

    public String getVersion() {
        return "null";
    }


}
