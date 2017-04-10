/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modulemanager;


import net.protium.api.Constant;
import net.protium.api.agents.CoreAgent;
import net.protium.api.agents.ModuleManager;
import net.protium.api.exceptions.AlreadyLoadedException;
import net.protium.api.module.Module;
import net.protium.core.utils.Functions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class Manager implements ModuleManager {

    private Map<String, Module> modules;
    private Map<String, Class> classes;

    public Manager() throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        CoreAgent.setModuleManager(this);
        modules = new HashMap<>();
        loadNotExistingModules();
    }

    @SuppressWarnings("WeakerAccess")
    private void loadNotExistingModules() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] modulesArr = Functions.listFiles(
                Functions.implode(Constant.getMOD_D(), File.separator),
                Constant.getMOD_EXT());
        for (String path:modulesArr) {
            String pathToJar = path;
            JarFile jarFile = new JarFile(pathToJar);
            URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            String name;
            name = Functions.getFileName(path);
            Class c = cl.loadClass(name);
            Module newModule = ((Module)c.newInstance());
            newModule.onEnable();
            modules.put(name, newModule);

        }
    }

    @Override
    public void unloadModule(String name) {
        if (!isLoaded(name)) {
            modules.get(name).onDisable();
            modules.remove(name);
        }
    }

    @Override
    public void loadModule(String name) {

    }

    public void loadNewModule(String name) throws AlreadyLoadedException {
        if (isLoaded(name)) {
            throw new AlreadyLoadedException();
        }
        String pathToJar = Functions.pathToFile(Constant.getMOD_D(), name, Constant.getMOD_EXT());
        //System.err.println(pathToJar);
        try {
            JarFile jarFile = new JarFile(pathToJar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{ new URL("jar:file:" + pathToJar+"!/") };
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        Class c = null;
        try {
            c = cl.loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Module newModule = null;
        try {
            newModule = ((Module)c.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        newModule.onEnable();
        modules.put(name, newModule);


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

    public void restartModule(String name) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        unloadModule(name);
        loadModule(name);
    }

    public void reloadModule(String name) {
        String pathToJar = Functions.pathToFile(Constant.getMOD_D(), name, Constant.getMOD_EXT());
        //System.err.println(pathToJar);
        try {
            JarFile jarFile = new JarFile(pathToJar);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{ new URL("jar:file:" + pathToJar+"!/") };
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        Class c = null;
        try {
            c = cl.loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Module newModule = null;
        try {
            newModule = ((Module)c.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        if (isLoaded(name)) {
            unloadModule(name);
        }
        newModule.onEnable();
        modules.put(name, newModule);
        classes.put(name, c);
    }

    private boolean isLoaded(String name) {
        return modules.containsKey(name);
    }

    @Override
    public Module getModule(String name) {
        return modules.get(name);
    }
}
