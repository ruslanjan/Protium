/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modulemanagement;


import net.protium.api.agents.CoreAgent;
import net.protium.api.agents.ModuleManager;
import net.protium.api.exceptions.NotFoundException;
import net.protium.api.module.Module;
import net.protium.core.utils.Constant;
import net.protium.core.utils.Functions;
import net.protium.core.utils.JSONParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager implements ModuleManager {

    private Map<String, Module> modules;
    private Map<String, Boolean> moduleStatus;
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
        loadAll();
    }

    private void loadAll() {
        String[] modulesArr = Functions.listFiles(
                Constant.MOD_D, ".jar");
        for (String path : modulesArr) {
            URL confUrl;
            try {
                confUrl = new URL("jar:file:" + path + "!/module.json");
            } catch (MalformedURLException e) {
                logger.log(Level.SEVERE, "Failed to create URL to module.json file", e);
                continue;
            }
            JSONParser config = null;
            try {
                config = new JSONParser(confUrl.openStream());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to open module.json in jarfile: " + path, e);
            }
            String mainClassPath = (String) config.get("mainClass");
            Class c;
            try {
                c = moduleClassLoader.loadClass(mainClassPath);
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Load Module FAILED: main Class not found", e);
                continue;
            }
            Module newModule;
            try {
                newModule = ((Module) c.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.log(Level.SEVERE, "FAILED: Can not create an instance of " + c.getName(), e);
                continue;
            }
            newModule.onEnable();
            String moduleName = (String) config.get("name");
            modules.put(moduleName, newModule);
            moduleStatus.put(moduleName, true);
        }
    }

    @Override
    public void reloadModules() {
        moduleClassLoader = new ModuleClassLoader(ClassLoader.getSystemClassLoader(),
                Functions.listFiles(Constant.MOD_D, ".jar"));
        loadAll();
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
        } else {
            logger.warning("Module '" + name + "' is already enabled");
        }
    }

    @Override
    public boolean getStatus(String name) {
        return moduleStatus.get(name);
    }

    @Override
    public Module getModule(String name) throws NotFoundException {
        if (!modules.containsKey(name)) {
            logger.log(Level.SEVERE, "no module with name: " + name);
            throw new NotFoundException();
        }
        return modules.get(name);
    }
}