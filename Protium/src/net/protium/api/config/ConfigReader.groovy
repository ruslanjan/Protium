/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.config

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.protium.api.exceptions.ArgumentException
import net.protium.api.exceptions.FileReadException
import net.protium.core.utils.Constant
import net.protium.core.utils.Functions

class ConfigReader {

    static String PATH_SEPARATOR = '.'
    static String PATH_SEPARATOR_REGEX = '\\.'

    static String INDEX_FIND_REGEX = /\[[0-9]+?]/
    static String INDEX_EXTRACT_REGEX = /[0-9]+/

    protected def data
    protected File file

    ConfigReader(configName) {
        String[] filePath = (Constant.CONF_D + [configName + Constant.CONF_EXT])
        file = new File(Functions.implode(filePath, File.separator))

        if (!file.exists()) {
            throw new FileNotFoundException()
        }

        data = openFile(file)

        if (data == null)
            throw new FileReadException()
    }

    static def openFile(File file) {
        (new JsonSlurper()).parse(file)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static String toPath(String[] arrayPath) {
        Functions.implode(arrayPath, PATH_SEPARATOR)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def get(String propertyPath) {
        String[] path = propertyPath.split(PATH_SEPARATOR_REGEX)

        def current = data

        while (1) {
            String siblingName = path[0]
            path = path.length > 1 ? path[1..-1] : []

            if (siblingName.matches(INDEX_FIND_REGEX)) {
                siblingName = (siblingName =~ INDEX_EXTRACT_REGEX)[0]
                current = (current as ArrayList)[siblingName as int]
            } else {
                current = current[siblingName]
            }

            if (path.length > 0 && current == null) {
                throw new ArgumentException()
            }

            if (path.length == 0) {
                break
            }
        }

        current
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    boolean checkPath(String propertyPath) {
        String[] path = propertyPath.split(PATH_SEPARATOR_REGEX)

        def current = data

        while (1) {
            String siblingName = path[0]
            path = path.length > 1 ? path[1..-1] : []

            if (siblingName.matches(INDEX_FIND_REGEX)) {
                siblingName = (siblingName =~ INDEX_EXTRACT_REGEX)[0]
                current = (current as ArrayList)[siblingName as int]
            } else {
                current = current[siblingName]
            }

            if (path.length > 0 && current == null) {
                return false
            }

            if (path.length == 0) {
                break
            }
        }

        current != null
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def get() { data }

    @SuppressWarnings("GroovyUnusedDeclaration")
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

    @SuppressWarnings("GroovyUnusedDeclaration")
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
