/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.agents.Config;
import net.protium.api.exceptions.FileReadException;
import net.protium.core.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;

public class RouteConfig extends Config {
    public RouteConfig(String configName) {
        super(configName);
    }
    @Override
    protected void init(String configName) throws FileReadException, FileNotFoundException {
        String filePath = (Constant.ROUTE_D + configName + Constant.CONF_EXT);
        file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        data = openFile(file);

        if (data == null)
            throw new FileReadException();
    }
}
