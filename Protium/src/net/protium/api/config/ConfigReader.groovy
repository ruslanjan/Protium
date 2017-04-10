/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.config

import groovy.json.JsonSlurper
import net.protium.core.Constant
import net.protium.api.exceptions.*
import net.protium.core.utils.Functions

import java.security.AccessController


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

        try {
            FilePermission fp = new FilePermission(file.getAbsolutePath(), "read")
            AccessController.checkPermission(fp)
        } catch (Exception ignored) {
            throw new FileReadException()
        }

        data = openFile(file)
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

        String currentPath = ""

        while (1) {
            String siblingName = path[0]
            path = path.length > 1 ? path[1..-1] : []

            currentPath += (currentPath.length() > 0 ? PATH_SEPARATOR : "") + siblingName

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
    def get() { data }
}
