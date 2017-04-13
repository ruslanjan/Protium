/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.exceptions.FileReadException;
import net.protium.core.utils.Constant;
import net.protium.core.utils.Functions;

import java.io.File;
import java.io.FileNotFoundException;

public class RouteConfig extends Config {

	public RouteConfig(String configName) {
		super(configName);
	}

	@Override
	protected void init(String configName) throws FileReadException, FileNotFoundException {
		String filePath = Functions.pathToFile(Constant.ROUTES_D, configName, ".json");
		file = new File(filePath);


		data = openFile(file);

		if (data == null)
			throw new FileReadException();
	}
}
