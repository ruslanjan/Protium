/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.request

import net.protium.api.events.Request
import net.protium.api.events.Response

/**
 * From: protium
 * Pkg: net.protium.modules.pauth
 * At: 15.04.17
 */
class Actions {

    static Response action$test1(Request request) {
        new PAResponse()
    }

}
