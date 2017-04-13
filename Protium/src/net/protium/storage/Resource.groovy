/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.storage

import net.protium.core.utils.Constant
import net.protium.core.utils.Functions

import javax.activation.MimeType

/**
 * From: protium
 * Pkg: net.protium.storage
 * At: 13.04.17
 */
class Resource {

    private File file
    private String textData
    private byte[] rawData
    private MimeType mimeType

    Resource(String name) {
        reload(name)
    }

    void reload(String name) {
        String path = (Constant.RES_D + name)
        file = new File(path)
        reload()
    }

    void reload() {
        textData = file.text
        rawData = file.bytes
        mimeType = new MimeType(textData)
    }

    String toString() { textData }

    byte[] toByteArray() { rawData }

    MimeType getMimeType() { mimeType }
}
