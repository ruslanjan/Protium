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
    private String rawData, action, url, queryString
    private Map headers

    HTTPRequest(String rawData, String queryString, Map headers) {
        this.rawData = rawData
        this.headers = headers
        this.queryString = queryString
    }

    @Override
    HashMap getSpecialData() {
        def arrData = rawData.split('&')

        HashMap formData = new HashMap<>()

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

    String setRawData(String data) { this.rawData = rawData }

    @Override
    String getURL() { url }

    @Override
    String getRawQueryString() { queryString }

    @Override
    HashMap getQuery() {
        def arrData = queryString.split('&')

        HashMap queryData = new HashMap<>()

        arrData.each { item ->
            item = item.split('=')
            queryData.put(item[0], item[1])
        }

        queryData
    }

    String setURL(String url) { this.url = url }

    @Override
    Map getHeaders() { headers }

    void setAction(String action) { this.action = action }

    @Override
    String getAction() { action }

    @Override
    String getMethod() { null }
}
