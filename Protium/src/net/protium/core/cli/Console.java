/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.cli;

import net.protium.Protium;
import net.protium.core.utils.Constant;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Console {

	private Map < String, Object > objectMap;
	private Map < String, Method > methodMap;
	private Logger logger = Logger.getLogger(getClass().getName());

	public Console( ) {
		try {
			logger.addHandler(new FileHandler(Constant.LOG_DIR + getClass() + Constant.LOG_EXT));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to create FileHandler", e);
		}
		objectMap = new HashMap <>();
		methodMap = new HashMap <>();
	}

	public void start( ) throws NoSuchMethodException {
		addCommand("reloadModules",
			Protium.manager,
			Protium.manager.getClass().getMethod("reloadModules")
		);

		loop();
	}

	private void loop( ) {
		Scanner sc = new Scanner(System.in);
		while (true) {
			if (sc.hasNextLine()) {
				String str = sc.nextLine();
				String[] strs = str.split("\\s+");

				if (strs[0].equals("stop")) {
					System.exit(0);
				}

				doCommand(strs);
			}
		}
	}

	private void addCommand(String name, Object object, Method method) {
		objectMap.put(name, object);
		methodMap.put(name, method);
	}

	private void doCommand(String[] args) {
		if (!methodMap.containsKey(args[0])) {
			logger.severe("Invalid command");
			return;
		}
		Collection < Object > objectCollection = new ArrayList <>();
		Class[] classes = methodMap.get(args[0]).getParameterTypes();
		if (classes.length != args.length) {
			logger.severe("Invalid command (args number doesn't match)");
			return;
		}
		for (int i = 0; i < args.length; ++i) {
			try {
				if (classes[i] != null) {
					objectCollection.add(classes[i].getConstructor(new Class[]{ String.class }).newInstance(args[i]));
				}
			} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
				logger.severe("Failed to execute command: " + Arrays.toString(args));
				return;
			}
		}
		try {
			methodMap.get(args[0]).invoke(objectMap.get(args[0]), objectCollection.toArray());
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.severe("Failed to execute command: " + Arrays.toString(args));
		}
	}
}
