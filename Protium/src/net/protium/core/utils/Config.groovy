/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.utils

import groovy.json.JsonOutput
import net.protium.api.exceptions.FileReadException
import net.protium.core.utils.AbstractJSONParser
import net.protium.core.utils.Constant
import net.protium.core.utils.Functions

class Config extends AbstractJSONParser {

    Config(String configName) {
        String[] filePath = (Constant.CONF_D + [configName + Constant.CONF_EXT])
        file = new File(Functions.implode(filePath, File.separator))

        if (!file.exists()) {
            throw new FileNotFoundException()
        }

        data = openFile(file)

        if (data == null)
            throw new FileReadException()
    }
}
