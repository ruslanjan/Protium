package net.protium.api.agents;

import net.protium.api.exceptions.FileReadException;
import net.protium.core.utils.AbstractJSONParser;
import net.protium.core.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Config extends AbstractJSONParser {
	public Config(String configName) throws IOException, FileReadException {
		init(configName);
	}

	protected void init(String configName) throws FileReadException, FileNotFoundException {
		String filePath = Functions.pathToFile(Constant.CONF_D, configName, Constant.CONF_EXT);

		file = new File(filePath);

		data = AbstractJSONParser.openFile(file);

		if (data == null) throw new FileReadException();
	}

	public static boolean createConfig(String configName) throws IOException {
		String filePath = Functions.createFile(Constant.CONF_D, configName, Constant.CONF_EXT);

		File file = new File(filePath);

		return file.exists() || ((file.getParentFile().mkdirs() || file.getParentFile().exists()) && file.createNewFile());

//		return false;
	}

}
