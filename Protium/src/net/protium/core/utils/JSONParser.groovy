/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.utils

import groovy.json.JsonException
import groovy.json.JsonSlurper

class JSONParser extends AbstractJSONParser {

    static def openStream(InputStream stream) throws JsonException {
        (new JsonSlurper()).parse(stream)
    }

    static def openString(String string) throws JsonException {
        (new JsonSlurper()).parse(string.toCharArray())
    }

    JSONParser(File file) throws JsonException {
        this.file = file
        data = openFile(file)
    }

    JSONParser(InputStream stream) throws JsonException {
        file = null
        data = openStream(stream)
    }

    JSONParser(String string) throws JsonException {
        file = null
        data = openString(string)
    }
}
