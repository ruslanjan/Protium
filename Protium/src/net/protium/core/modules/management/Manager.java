/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modules.management;


import net.protium.api.agents.CoreAgent;
import net.protium.api.agents.ModuleManager;
import net.protium.api.exceptions.NotFoundException;
import net.protium.api.module.Module;
import net.protium.core.gui.MainApp;
import net.protium.core.utils.Constant;
import net.protium.api.agents.Functions;
import net.protium.core.utils.JSONParser;
import net.protium.core.utils.Pair;

import java.io.IOException;
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

    private Map<String, Module> modules;
    private Map<String, Boolean> moduleStatus;
    private Map<String, String> status;
    private ModuleClassLoader moduleClassLoader;
    private static Logger logger = Logger.getLogger(Manager.class.getName());

    public Manager() {
        try {
            logger.addHandler((new FileHandler(
                    Functions.createFile(Constant.LOG_D, this.getClass().getName(), Constant.LOG_EXT))));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write logs", e);
        }
        CoreAgent.setModuleManager(this);
        modules = new HashMap<>();
        moduleStatus = new HashMap<>();
        moduleClassLoader = new ModuleClassLoader(ClassLoader.getSystemClassLoader(),
                Functions.listFiles(Constant.MOD_D, ".jar"));
        status = new HashMap<>();
        loadAll();
    }

    private void loadAll() {
        status.clear();
        modules.clear();
        moduleStatus.clear();
        String[] modulesArr = Functions.listFiles(
                Constant.MOD_D, ".jar");
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
            if (modules.containsKey(config.get("name"))) {
                logger.warning("Module duplicated: " + config.getString("name"));
                status.put(statusName, "WAR");
                continue;
            }
            statusName = config.getString("name");
            String mainClassPath = (String) config.get("mainClass");
            Class c;
            try {
                c = moduleClassLoader.loadClass(mainClassPath);
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Load Module FAILED: main Class not found", e);
                status.put(statusName, "ERR");
                continue;
            }
            Module newModule;
            try {
                newModule = ((Module) c.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.log(Level.SEVERE, "FAILED: Can not create an instance of " + c.getName(), e);
                status.put(statusName, "ERR");
                continue;
            }
            newModule.onEnable();
            String moduleName = (String) config.get("name");
            modules.put(moduleName, newModule);
            moduleStatus.put(moduleName, true);
            status.put(statusName, "ON");
        }
    }

    @Override
    public void reloadModules() {
        moduleClassLoader = new ModuleClassLoader(ClassLoader.getSystemClassLoader(),
                Functions.listFiles(Constant.MOD_D, ".jar"));
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
            modules.get(name).onDisable();
            moduleStatus.put(name, false);
            MainApp.controller.reloadModuleList();
            status.put(name, "OFF");
            logger.info("Module '" + name + "' is disabled");
        } else {
            logger.warning("Module '" + name + "' is already disabled");
        }
    }

    @Override
    public void enableModule(String name) throws NotFoundException {
        if (!modules.containsKey(name)) {
            logger.log(Level.SEVERE, "no module with name: " + name);
            throw new NotFoundException();
        }
        if (!getStatus(name)) {
            modules.get(name).onEnable();
            moduleStatus.put(name, true);
            logger.info("Module '" + name + "' is enabled");
            status.put(name, "ON");
            MainApp.controller.reloadModuleList();
        } else {
            logger.warning("Module '" + name + "' is already enabled");
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

    @Override
    public Module getModule(String name) throws NotFoundException {
        if (!modules.containsKey(name)) {
            logger.log(Level.SEVERE, "no module with name: " + name);
            throw new NotFoundException();
        }
        return modules.get(name);
    }

    @Override
    public Collection<Pair<String, String>> getModulesAsString() {
        Collection<Pair<String, String>> collection = new ArrayList<>();
        for (Map.Entry<String, String> entry: status.entrySet()) {
            collection.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        return collection;
    }

    @Override
    protected void finalize() throws Throwable {
        for (Map.Entry<String, Module> entry: modules.entrySet()) {
            entry.getValue().onDisable();
        }
        super.finalize();
    }

    private boolean checkJSONSchema(JSONParser config) {
        if (!config.checkPath("name") || !config.checkPath("mainClass")) {
            return false;
        }
        return true;
    }
}