/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modulemanager;


import net.protium.api.Constant;
import net.protium.api.agents.CoreAgent;
import net.protium.api.agents.ModuleManager;
import net.protium.api.module.Module;
import net.protium.core.utils.Functions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.io.File.separator;

public class Manager implements ModuleManager {

    private Map<String, Module> modules;

    public Manager() {
        CoreAgent.setModuleManager(this);
        modules = new HashMap<>();
        loadNotExistingModules();
    }

    @SuppressWarnings("WeakerAccess")
    public void loadNotExistingModules() {

    }

    @SuppressWarnings("WeakerAccess")
    public void unloadModule(String name) {
        if (!isLoaded(name)) {
            modules.get(name).onDisable();
            modules.remove(name);
        }
    }

    public void loadModule(String name) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String pathToJar = Functions.pathToFile(Constant.getMOD_D(), name, Constant.getMOD_EXT());
        System.err.println(pathToJar);
        JarFile jarFile = new JarFile(pathToJar);
        URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);
        JarEntry je = jarFile.getJarEntry(name);
        System.err.println(je.getName());
        /*
        load all JarEntry class
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            System.err.println(je.getName());
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);
            //odule nw = ((Module)c.newInstance());
            //nw.onEnable();
        }
        */
    }

    public void reloadModule(String name) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        unloadModule(name);
        loadModule(name);
    }

    private boolean isLoaded(String name) {
        return modules.containsKey(name);
    }

    @Override
    public Module getModule(String name) {
        return modules.get(name);
    }
}
