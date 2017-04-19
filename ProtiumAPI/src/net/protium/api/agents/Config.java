/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import groovy.json.JsonException;
import net.protium.api.exceptions.FileReadException;
import net.protium.api.utils.AbstractJSONParser;
import net.protium.api.utils.Constant;
import net.protium.api.utils.Functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class Config extends AbstractJSONParser {

	public Config(String configName) throws IOException, FileReadException {
		this(configName, null);
	}

	public Config(String configName, String schema) throws IOException, FileReadException {
		init(configName, schema);
	}

	public static boolean createConfig(String configName) throws IOException {
		String filePath = Functions.createFile(Constant.CONF_DIR, configName, Constant.CONF_EXT);

		File file = new File(filePath);

		boolean success = file.exists() || ((file.getParentFile().mkdirs() || file.getParentFile().exists()) && file.createNewFile());

		URL url = new URL(file.getAbsolutePath());

		url.openConnection().getOutputStream().write("null".getBytes());

		return success;
	}

	public static boolean configExists(String configName) throws IOException {
		String filePath = Functions.createFile(Constant.CONF_DIR, configName, Constant.CONF_EXT);

		return (new File(filePath)).exists();
	}

	protected void init(String configName, String schema) throws FileReadException, FileNotFoundException, JsonException {
		String filePath = Functions.pathToFile(Constant.CONF_DIR, configName, Constant.CONF_EXT);

		file = new File(filePath);

		data = AbstractJSONParser.openFile(file);

		if (data == null)
			throw new FileReadException();

		if (schema != null && !validate(schema)) {
			throw new JsonException();
		}
	}

}
