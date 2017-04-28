/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modules.management;

import net.protium.api.utils.Constant;
import net.protium.api.utils.Functions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("Duplicates")
class ModuleClassLoader extends ClassLoader {
	private static final Logger logger = Logger.getLogger(ClassLoader.class.getName());
	private final String[] modules;
	private final ClassLoader parent;

	ModuleClassLoader(ClassLoader parent, String[] modules) {
		try {
			logger.addHandler((new FileHandler(
				Functions.createFile(Constant.LOG_DIR, this.getClass().getName(), Constant.LOG_EXT))));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to write logs", e);
		}
		this.parent = parent;
		this.modules = modules.clone();
	}

	private static String loadPackage(Class c) {
		String[] data = c.getName().split("\\.");
		data = Arrays.copyOf(data, data.length - 1);

		return Functions.implode(data, ".");
	}

	@Override
	public Class loadClass(String name) throws ClassNotFoundException {
		try {
			Class c = findLoadedClass(name);
			return (c != null) ? c : parent.loadClass(name);
		} catch (ClassNotFoundException ignored) {
			String path = name.replace('.', '/') + ".class";
			for (String module : modules) {
				URL url;
				try {
					url = new URL("jar:file:" + module + "!/" + path);
				} catch (MalformedURLException e) {
					logger.log(Level.SEVERE, "Failed to open url: " + "jar:file:" + Constant.MOD_DIR + module + "!/" + path, e);
					continue;
				}
				try {
					URLConnection connection = url.openConnection();
					InputStream input = connection.getInputStream();
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int data;
					data = input.read();
					while (data != -1) {
						buffer.write(data);
						data = input.read();
					}
					input.close();
					byte[] classData = buffer.toByteArray();

					Class c = defineClass(name,
						classData, 0, classData.length);

					String pkgName = loadPackage(c);

					if (getPackage(pkgName) == null) {
						definePackage(
							pkgName,
							"", "", "",
							"", "", "",
							null);
					}

					return c;
				} catch (IOException ignored1) {
				}
			}
		}
		return null;
	}

	@Override
	protected URL findResource(String name) {
		for (String module : modules) {
			URL url;

			try {
				url = new URL("jar:file:" + module + "!/" + name);
			} catch (MalformedURLException e) {
				logger.log(Level.SEVERE, "Failed to open url: " + "jar:file:" + Constant.MOD_DIR + module + "!/" + name, e);
				continue;
			}

			try {
				URLConnection connection = url.openConnection();
				InputStream inputStream = connection.getInputStream();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int data;
				data = inputStream.read();

				while (data != -1) {
					buffer.write(data);
					data = inputStream.read();
				}

				inputStream.close();

			} catch (IOException e) {
				continue;
			}

			return url;
		}

		return null;
	}

	@Override
	protected Enumeration < URL > findResources(String name) {

		List < URL > urls = new ArrayList <>();

		for (String module : modules) {
			URL url;

			try {
				url = new URL("jar:file:" + module + "!/" + name);
			} catch (MalformedURLException e) {
				logger.log(Level.SEVERE, "Failed to open url: " + "jar:file:" + Constant.MOD_DIR + module + "!/" + name, e);
				continue;
			}

			try {
				URLConnection connection = url.openConnection();
				InputStream inputStream = connection.getInputStream();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int data;
				data = inputStream.read();

				while (data != -1) {
					buffer.write(data);
					data = inputStream.read();
				}

				inputStream.close();

			} catch (IOException e) {
				continue;
			}

			urls.add(url);
		}

		return Collections.enumeration(urls);
	}
}
