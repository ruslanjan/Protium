package net.protium.core.utils

import groovy.json.JsonSlurper

@SuppressWarnings("GroovyUnusedDeclaration")
class JSONParser extends AbstractJSONParser {

    static def openStream(InputStream stream) {
        (new JsonSlurper()).parse(stream)
    }

    static def openString(String string) {
        (new JsonSlurper()).parse(string.toCharArray())
    }

    JSONParser(File file) {
        this.file = file
        data = openFile(file)
    }

    JSONParser(InputStream stream) {
        file = null
        data = openStream(stream)
    }

    JSONParser(String string) {
        file = null
        data = openString(string)
    }
}
