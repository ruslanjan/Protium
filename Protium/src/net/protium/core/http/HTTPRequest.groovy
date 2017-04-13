/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http

import net.protium.api.events.Request

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 10.04.17
 */
class HTTPRequest implements Request {
    private String rawData, action, url
    private Map headers

    HTTPRequest(String rawData, Map headers) {
        this.rawData = rawData
        this.headers = headers
    }

    @Override
    Map getSpecialData() {
        def arrData = rawData.split('&')

        def formData = new HashMap<>()

        arrData.each { item ->
            item = item.split('=')
            formData.put(item[0], item[1])
        }

        formData
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    Object setHeaders(Map headers) {
        this.headers = headers
    }

    @Override
    String getRawData() { rawData }

    @SuppressWarnings("GroovyUnusedDeclaration")
    String setRawData(String data) { this.rawData = rawData }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @Override
    String getURL() { url }

    @SuppressWarnings("GroovyUnusedDeclaration")
    String setURL(String url) { this.url = url }

    @Override
    Map getHeaders() { headers }

    @SuppressWarnings("GroovyUnusedDeclaration")
    void setAction(String action) { this.action = action }

    @Override
    String getAction() { action }

    @Override
    String getMethod() { null }
}
