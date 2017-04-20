/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.http
/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 10.04.17
 */
class HTTPResponse implements Response {

    String contentType, response
	Map<String, ?> headers = new HashMap<>()
    Integer status

	@Override
	Map<String, ?> getHeaders() {
		return headers
	}

	void setHeaders(Map<String, ?> headers) {
		this.headers = headers
	}

	@Override
    String getContentType() { contentType }

    void setContentType(String type) {
        contentType = type
    }

    @Override
    String getResponse() {
        response
    }

    void setResponse(String response) {
        this.response = response
    }

    @Override
    Integer getStatus() {
        status
    }
}
