/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modules.management;


import net.protium.Protium;
import net.protium.api.agents.ModuleManager;
import net.protium.api.annotations.OnDisable;
import net.protium.api.annotations.OnEnable;
import net.protium.api.exceptions.NotFoundException;
import net.protium.api.module.AbstractModule;
import net.protium.api.module.IModule;
import net.protium.api.utils.Constant;
import net.protium.api.utils.Functions;
import net.protium.api.utils.Pair;
import net.protium.core.gui.MainApp;
import net.protium.core.utils.JSONParser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager implements ModuleManager {

	private static final Logger logger = Logger.getLogger(Manager.class.getName());
	private final Map < String, IModule > modules;
	private final Map < String, Boolean > moduleStatus;
	private final Map < String, String > status;
	private final Map < String, String > modulesURLMap;
	private ModuleClassLoader moduleClassLoader;

	public Manager( ) {
		try {
			logger.addHandler((new FileHandler(
				Functions.createFile(Constant.LOG_DIR, this.getClass().getName(), Constant.LOG_EXT))));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to write logs", e);
		}

		modules = new HashMap <>();
		moduleStatus = new HashMap <>();
		moduleClassLoader = new ModuleClassLoader(ClassLoader.getSystemClassLoader(),
			Functions.listFiles(Constant.MOD_DIR, ".jar"));
		status = new HashMap <>();
		modulesURLMap = new HashMap <>();

		AbstractModule.moduleManager = this;
		loadAll();
	}

	private void loadAll( ) {
		status.clear();
		modules.clear();
		moduleStatus.clear();
		modulesURLMap.clear();
		String[] modulesArr = Functions.listFiles(
			Constant.MOD_DIR, ".jar");
		for (String path : modulesArr) {
			String statusName = path;
			URL confUrl;
			try {
				confUrl = new URL("jar:file:" + path + "!/module.json");
			} catch (MalformedURLException e) {
				logger.log(Level.SEVERE, "Failed to create URL to module.json file", e);
				status.put(statusName, "ERR");
				continue;
			}
			JSONParser config;
			try {
				config = new JSONParser(confUrl.openStream());
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to open module.json in jarfile: " + path, e);
				status.put(statusName, "ERR");
				continue;
			}
			if (!checkJSONSchema(config)) {
				logger.severe("Invalid schema in module.json in jar: " + path);
				status.put(statusName, "ERR");
				continue;
			}
			if (modules.containsKey(config.getString("id"))) {
				logger.warning("IModule duplicated: " + config.getString("id"));
				status.put(statusName, "WAR");
				continue;
			}
			statusName = config.getString("id");
			String mainClassPath = (String) config.get("mainClass");
			Class c;
			try {
				c = moduleClassLoader.loadClass(mainClassPath);
			} catch (ClassNotFoundException e) {
				logger.log(Level.SEVERE, "Load IModule FAILED: main Class not found", e);
				status.put(statusName, "ERR");
				continue;
			}
			IModule newModule;
			try {
				newModule = ((IModule) c.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				logger.log(Level.SEVERE, "FAILED: Can not create an instance of " + c.getName(), e);
				status.put(statusName, "ERR");
				continue;
			}
			String moduleName = (String) config.get("id");
			try {
				AnnotationUtil.invokeMethodWithAnnotation(moduleName, OnEnable.class, new Class[]{ });
			} catch (Exception e) {
				logger.severe("Unhandled exception in module: " + moduleName);
			}
			modules.put(moduleName, newModule);
			moduleStatus.put(moduleName, true);
			status.put(statusName, "on");
			modulesURLMap.put(moduleName, ("jar:file:" + path + "!/"));
		}
	}

	@Override
	public void reloadModules( ) {
		for (Map.Entry < String, IModule > entry : modules.entrySet()) {
			AnnotationUtil.invokeMethodWithAnnotation(entry.getValue(), OnDisable.class, new Class[]{ });
		}
		moduleClassLoader = new ModuleClassLoader(ClassLoader.getSystemClassLoader(),
			Functions.listFiles(Constant.MOD_DIR, ".jar"));
		loadAll();
		MainApp.controller.reloadModuleList();
	}

	@Override
	public void disableModule(String name) throws NotFoundException {
		if (!modules.containsKey(name)) {
			logger.log(Level.SEVERE, "no module with name: " + name);
			throw new NotFoundException();
		}
		if (getStatus(name)) {
			try {
				AnnotationUtil.invokeMethodWithAnnotation(modules.get(name), OnDisable.class, new Class[]{ });
			} catch (Exception e) {
				logger.severe("Unhandled exception in module: " + name);
			}
			moduleStatus.put(name, false);
			MainApp.controller.setModuleViewStatus(name, "off");
			status.put(name, "off");
			logger.info("IModule '" + name + "' is disabled");
		} else {
			logger.warning("IModule '" + name + "' is already disabled");
		}
	}

	@Override
	public void enableModule(String name) throws NotFoundException {
		if (!modules.containsKey(name)) {
			logger.log(Level.SEVERE, "no module with name: " + name);
			throw new NotFoundException();
		}
		if (!getStatus(name)) {
			try {
				AnnotationUtil.invokeMethodWithAnnotation(modules.get(name), OnEnable.class, new Class[]{ });
			} catch (Exception e) {
				logger.severe("Unhandled exception in module: " + name);
			}
			moduleStatus.put(name, true);
			MainApp.controller.setModuleViewStatus(name, "on");
			status.put(name, "on");
			logger.info("IModule '" + name + "' is enabled");
		} else {
			logger.warning("IModule '" + name + "' is already enabled");
		}
	}

	@Override
	public boolean getStatus(String name) {
		return moduleStatus.get(name);
	}

	@Override
	public String getExtendedStatus(String name) {
		return status.get(name);
	}

	/**
	 * Gets module with specified id.
	 *
	 * @param name Target module name
	 * @return IModule Target module object or null if target module is disabled.
	 * @throws NotFoundException If target module can't be found.
	 */
	@Override
	public IModule getModule(String name) throws NotFoundException {
		if (!modules.containsKey(name)) {
			logger.log(Level.SEVERE, "no module with name: " + name);
			throw new NotFoundException();
		}
		if (!moduleStatus.get(name)) {
			return null;
		}
		return modules.get(name);
	}

	@Override
	public URL getModuleResourceURL(String name, String path) throws NotFoundException {
		if (!modulesURLMap.containsKey(name)) {
			logger.log(Level.SEVERE, "no module URL with name: " + name);
			throw new NotFoundException();
		}
		//
		try {
			return new URL(modulesURLMap.get(name) + path);
		} catch (MalformedURLException e) {
			logger.severe("Failed to get URL of: " + modulesURLMap.get(name) + path);
		}
		return null;
	}

	@Override
	public void setModuleViewStatus(String ModuleName, String status) throws NotFoundException {
		if (!this.status.containsKey(ModuleName)) {
			logger.log(Level.SEVERE, "no moduleStatus with name: " + ModuleName);
			throw new NotFoundException();
		}
		this.status.put(ModuleName, status);
		MainApp.controller.setModuleViewStatus(ModuleName, status);
	}

	@Override
	public Collection < Pair < String, String > > getModulesAsString( ) {
		Collection < Pair < String, String > > collection = new ArrayList <>();
		for (Map.Entry < String, String > entry : status.entrySet()) {
			collection.add(new Pair <>(entry.getKey(), entry.getValue()));
		}
		return collection;
	}

	public JSONParser getModuleConfig(String name) {
		URL confUrl;
		try {
			confUrl = (Protium.manager.getModuleResourceURL(name, "module.json"));
		} catch (NotFoundException e) {
			logger.log(Level.SEVERE, "Failed to create URL to module.json file", e);
			return null;
		}
		JSONParser config = null;
		try {
			config = new JSONParser(confUrl.openStream());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to open module.json in jarfile: " + confUrl.toString(), e);
		}
		return config;
	}

	@Override
	protected void finalize( ) throws Throwable {
		for (Map.Entry < String, IModule > entry : modules.entrySet()) {
			AnnotationUtil.invokeMethodWithAnnotation(entry.getValue(), OnDisable.class, new Class[]{ });
		}
		super.finalize();
	}

	private boolean checkJSONSchema(JSONParser config) {
		return !(!config.checkPath("id") || !config.checkPath("mainClass"));
	}

	private static class AnnotationUtil {
		public static void invokeMethodWithAnnotation(Object obj, Class annotation, Object[] args) {
			for (Method method : obj.getClass().getMethods()) {
				Object object = method.getAnnotation(annotation);
				if (object != null) {
					try {
						method.invoke(obj, args);
					} catch (IllegalAccessException | InvocationTargetException e) {
						logger.log(Level.SEVERE, "Invalid args in method: " + method.getName(), e);
					}
				}
			}
		}
	}
}