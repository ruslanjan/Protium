/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modulemanager;


import net.protium.api.agents.CoreAgent;
import net.protium.api.agents.ModuleManager;
import net.protium.api.module.Module;
import net.protium.core.Constant;
import net.protium.core.utils.Functions;
import org.apache.tools.ant.taskdefs.Classloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager implements ModuleManager {

    private Map<String, Module> modules;
    private Map<String, Class> classes;
    private Map<String, Boolean> moduleStatus;
    private static Logger logger = Logger.getLogger(Manager.class.getName());

    public Manager() {
        try {
            logger.addHandler((new FileHandler(Constant.getLOG_FOLDER() + Manager.class.getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CoreAgent.setModuleManager(this);
        modules = new HashMap<>();
        classes = new HashMap<>();
        moduleStatus = new HashMap<>();
        //loadAll();
    }

    @SuppressWarnings("WeakerAccess")
    private void loadAll() {
        String[] modulesArr = Functions.listFiles(
                Functions.implode(Constant.getMOD_D(), File.separator),
                Constant.getMOD_EXT());
        for (String path : modulesArr) {
            String pathToJar = path;
            try {
                JarFile jarFile = new JarFile(pathToJar);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Load Module FAILED: Can't open jarFile: " + pathToJar, e);
                continue;
            }
            URL[] urls = new URL[0];
            try {
                urls = new URL[]{new URL("jar:file:" + pathToJar + "!/")};
            } catch (MalformedURLException e) {
                logger.log(Level.SEVERE, "Load Module FAILED: Can't make urls", e);
                continue;
            }
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            String name;
            name = Functions.getFileName(path);
            Class c = null;
            try {
                c = cl.loadClass(name);
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Load Module FAILED: Class not found", e);
                continue;
            }
            Module newModule = null;
            try {
                newModule = ((Module) c.newInstance());
            } catch (InstantiationException e) {
                logger.log(Level.SEVERE, "Load Module FAILED: Instantiation Exception", e);
                continue;
            } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, "Load Module FAILED: Illegal Access Exception", e);
                continue;
            }
            newModule.onEnable();
            System.out.println(name);
            modules.put(name, newModule);
            classes.put(name, c);
            moduleStatus.put(name, true);
        }
    }

    @Override
    public void unloadModule(String name) {
        disableModule(name);
        modules.remove(name);
        moduleStatus.remove(name);
        classes.remove(name);
        logger.info("Module '" + name + "' unloaded!");
    }

    @Override
    public void loadModule(String name) {
        String pathToJar = Functions.pathToFile(Constant.getMOD_D(), name, Constant.getMOD_EXT());
        //System.err.println(pathToJar);
        try {
            JarFile jarFile = new JarFile(pathToJar);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "reloadModule FAILED: Can't open jar file '" + pathToJar + "'", e);
            return;
        }
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{new URL("jar:file:" + pathToJar + "!/")};
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "reloadModule FAILED: Can't make urls", e);
            return;
        }
        for (URL ur:urls) {
            System.err.println(ur.toString());
        }
        System.err.println("-------------------");
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        Class c = null;
        try {
            c = cl.loadClass(name);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "reloadModule FAILED: Class not found", e);
        }
        Module newModule = null;
        try {
            newModule = ((Module) c.newInstance());
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "reloadModule FAILED: Instantiation Exception", e);
            return;
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "reloadModule FAILED: Illegal Access Exception", e);
            return;
        }
        newModule.onEnable();
        modules.put(name, newModule);
        classes.put(name, c);
        moduleStatus.put(name, true);
        logger.info("Module '" + name + "' loaded!");
    }

    @Override
    public void reloadModule(String name) {
        unloadModule(name);
        loadModule(name);
    }

    @Override
    public void restartModule(String name) {
        disableModule(name);
        enableModule(name);
    }

    @Override
    public void disableModule(String name) {
        if (!classes.containsKey(name)) {
            logger.warning("Module '" + name + "' does't loaded");
        } else {
            if (getStatus(name)) {
                modules.get(name).onDisable();
                moduleStatus.put(name, false);
            }
            logger.info("Module '" + name + "' is disabled");
        }
    }

    @Override
    public void enableModule(String name) {
        if (!classes.containsKey(name)) {
            logger.severe("Module '" + name + "' does't loaded");
        } else {
            if (!getStatus(name)) {
                modules.get(name).onEnable();
                moduleStatus.put(name, true);
            }
            logger.info("Module '" + name + "' is enabled");
        }
    }

    private boolean isLoaded(String name) {
        return classes.containsKey(name);
    }

    private boolean getStatus(String name) {
        return moduleStatus.get(name);
    }

    @Override
    public Module getModule(String name) {
        return modules.get(name);
    }
}