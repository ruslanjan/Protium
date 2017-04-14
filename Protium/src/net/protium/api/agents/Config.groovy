/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents

import net.protium.api.exceptions.FileReadException
import net.protium.core.utils.AbstractJSONParser
import net.protium.core.utils.Constant
import net.protium.core.utils.Functions

class Config extends AbstractJSONParser {

    Config(String configName) {
        init(configName)
    }

    protected void init(String configName) throws FileReadException, FileNotFoundException {
        String filePath = Functions.pathToFile(Constant.CONF_D, configName, Constant.CONF_EXT)

        file = new File(filePath)

        data = openFile(file)

        if (data == null)
            throw new FileReadException()
    }
}
