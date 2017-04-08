/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.module;

import net.protium.api.event.Request;
import net.protium.api.event.Response;

public interface Module {
    void postInstall();

    void init();

    Response onRequest(Request request);

    void destroy();
}
