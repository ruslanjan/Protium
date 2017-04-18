/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.request

import net.protium.api.events.Response

/**
 * From: protium
 * Pkg: net.protium.modules.pauth.request
 * At: 15.04.17
 */
class PAResponse implements Response {

    String response

    @Override
    String getContentType() {
        return "text/html; charset=utf-8"
    }

    @Override
    Integer getStatus() {
        return 200
    }
}
