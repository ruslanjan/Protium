package net.protium.core.utils

import groovy.json.JsonSlurper

class JSONParser extends AbstractJSONParser {

    static def openStream(InputStream stream) {
        (new JsonSlurper()).parse(stream)
    }

    static def openString(String string) {
        (new JsonSlurper()).parse(string.toCharArray())
    }

    JSONParser(File file) {
        file = jsonFile
        data = openFile(file)
    }

    JSONParser(InputStream stream) {
        file = null
        data = openStream(dataStream)
    }

    JSONParser(String string) {
        file = null
        data = openString(string)
    }
}
