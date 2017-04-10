/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http

import net.protium.api.event.Response

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 10.04.17
 */
class HTTPResponse implements Response {

    String contentType, response

    @Override
    String getContentType() { contentType }

    @Override
    void setContentType(String type) {
        contentType = type
    }

    @Override
    String getResponse() {
        response
    }

    @Override
    void setResponse(String response) {
        this.response = response
    }
}
