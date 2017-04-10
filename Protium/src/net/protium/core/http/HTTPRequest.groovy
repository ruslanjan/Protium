/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http

import net.protium.api.event.Request

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 10.04.17
 */
class HTTPRequest implements Request {
    String rawData, action
    Map headers

    HTTPRequest(String rawData, Map headers) {
        this.rawData = rawData
        this.formData = null
        this.headers = headers
    }

    @Override
    Map getSpecialData() {
        def arrData = rawData.split('&')

        formData = new HashMap<>()

        arrData.each { item ->
            item = item.split('=')
            formData.put(item[0], item[1])
        }

        formData
    }

    @Override
    Object setHeaders(Map headers) {
        this.headers = headers
    }

    @Override
    String getRawData() { rawData }

    @Override
    String setRawData(String data) { this.rawData = rawData }

    @Override
    Map getHeaders() { headers }

    @Override
    void setAction(String action) {
        this.action = action
    }

    @Override
    String getAction() { action }

    @Override
    String getMethod() {
        return null
    }
}
