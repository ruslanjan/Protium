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

public class Manager implements ModuleManager {

    private Map<String, Module> modules;

    public Manager() {
        CoreAgent.setModuleManager(this);
        modules = new HashMap<>();
        loadNotExistingModules();
    }

    public void loadNotExistingModules() {

    }

    public void unloadModule(String name) {
        if (!isLoaded(name)) {
            modules.get(name).onDisable();
            modules.remove(name);
        }
    }

    public void loadModule(String name) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] moduleArr =
                Functions.listFiles(
                        Functions.implode(Constant.getMOD_D(), File.separator),
                        Constant.getMOD_EXT());
        for (String pathToJar:moduleArr) {
            System.err.println(pathToJar);
            JarFile jarFile = new JarFile(pathToJar);
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if(je.isDirectory() || !je.getName().endsWith(".class")){
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0,je.getName().length()-6);
                className = className.replace('/', '.');
                Class c = cl.loadClass(className);
                Module nw = ((Module)c.newInstance());
                nw.onEnable();
            }
        }
    }

    public void reloadModule(String name) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        unloadModule(name);
        loadModule(name);
    }

    private boolean isLoaded(String name) {
        if (modules.containsKey(name)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Module getModule(String name) {
        return modules.get(name);
    }
}
