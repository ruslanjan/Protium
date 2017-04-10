/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.config

import groovy.json.JsonOutput

@SuppressWarnings("GroovyUnusedDeclaration")
class ConfigWriter extends ConfigReader {

    @SuppressWarnings("GroovyUnusedDeclaration")
    ConfigWriter(Object configName) {
        super(configName)
    }

    void set(String path, value) {
        data = setRecursive(data, path.split(PATH_SEPARATOR_REGEX), value)
        this
    }

    protected def setRecursive(object, String[] path, value) {
        if (path.length > 0) {
            object[path[0]] = setRecursive(object[path[0]], (path.length > 1 ? path[1..-1] : []) as String[], value)
            object
        } else {
            value
        }
    }

    void commit(boolean prettyPrint = true) {
        FileWriter writer = new FileWriter(file, false)
        writer.write(
                prettyPrint ?
                        JsonOutput.prettyPrint(JsonOutput.toJson(data)) :
                        JsonOutput.toJson(data)
        )
        writer.close()
    }
}
