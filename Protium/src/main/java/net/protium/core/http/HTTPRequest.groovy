/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http

import net.protium.api.http.Request

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 10.04.17
 */
class HTTPRequest implements Request {
    private String rawData, action, queryString, url
    private Map headers, options

    HTTPRequest(String rawData, String queryString, Map headers) {
        this.rawData = rawData
        this.headers = headers
        this.queryString = queryString
    }

    @Override
    Map getSpecialData() {
        def arrData = rawData.split('&')

        Map formData = new HashMap<>()

        arrData.each { item ->
            item = item.split('=')
            formData.put(item[0], item[1])
        }

        formData
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    void setHeaders(Map headers) {
        this.headers = headers as HashMap
    }

    @Override
    String getRawData() { rawData }

    @Override
    Map getOptions() { options }

    @Override
    String getURL() { url }

    @Override
    String getRawQueryString() { queryString }

    @Override
    Map getQuery() {
        def arrData = queryString.split('&')

        Map queryData = new HashMap<>()

        arrData.each { item ->
            item = item.split('=')
            queryData.put(item[0], item[1])
        }

        queryData
    }

    void setOptions(Map options) { this.options = options }

    @Override
    Map getHeaders() { headers }

    void setAction(String action) { this.action = action }

    void setURL(String url) { this.url = url }

    @Override
    String getAction() { action }

    @Override
    String getMethod() { null }
}
