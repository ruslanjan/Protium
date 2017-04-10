/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 10.04.17
 */
class HTTPData {
    String rawData
    Map formData
    Map headers

    HTTPData(String rawData, Map headers) {
        this.rawData = rawData
        this.formData = null
        this.headers = headers
    }

    Map getFormData() {
        if (formData != null)
            return formData

        def arrData = rawData.split('&')

        formData = new HashMap<>()

        arrData.each { item ->
            item = item.split('=')
            formData.put(item[0], item[1])
        }

        formData
    }

    String getRawData() { rawData }

    Map getHeaders() { headers }
}
