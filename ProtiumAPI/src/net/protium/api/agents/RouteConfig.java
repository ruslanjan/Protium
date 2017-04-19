/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.exceptions.FileReadException;
import net.protium.api.utils.Constant;

import java.io.File;
import java.io.IOException;

public class RouteConfig extends Config {

	public RouteConfig(String configName) throws IOException, FileReadException {
		super(configName);
	}

	@Override
	protected void init(String configName) throws FileReadException {
		String filePath = Functions.pathToFile(Constant.ROUTES_DIR, configName, ".json");
		file = new File(filePath);


		data = openFile(file);

		if (data == null)
			throw new FileReadException();
	}
}
