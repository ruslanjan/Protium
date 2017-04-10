/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http

import javax.servlet.http.HttpServletRequest

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 10.04.17
 */
class HTTPRequestParser {

    HttpServletRequest request
    Map headers

    HTTPRequestParser(HttpServletRequest request) {
        this.request = request
    }

    String getContentType() {
        def metaData = request.getHeader("Content-Type")
        metaData
    }

    private Map getHeaders() {
        if(this.headers != null)
            return this.headers
        def headerNames = request.getHeaderNames()
        Map result = new HashMap<>()
        headerNames.each { item ->
            result.put(item, request.getHeader(item as String))
        }
        result
    }

    HTTPRequest getData() {
        InputStream input = request.getInputStream()
        assert input != null
        byte[] buffer = new byte[1024]
        StringBuilder rawInput = new StringBuilder("")
        while (true) {
            int gotBytes = input.read(buffer)
            if (gotBytes <= 0)
                break
            rawInput.append(new String(buffer))
        }
        new HTTPRequest(rawInput.toString(), getHeaders())
    }

}
