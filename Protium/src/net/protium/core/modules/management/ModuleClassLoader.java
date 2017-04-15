/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modules.management;

import net.protium.core.utils.Constant;
import net.protium.core.utils.Functions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class ModuleClassLoader extends ClassLoader {
    private static Logger logger = Logger.getLogger(ClassLoader.class.getName());
    private String[] modules;
    private ClassLoader parent;

    ModuleClassLoader(ClassLoader parent, String[] modules) {
        try {
            logger.addHandler((new FileHandler(
                    Functions.createFile(Constant.LOG_D, this.getClass().getName(), Constant.LOG_EXT))));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write logs", e);
        }
        this.parent = parent;
        this.modules = modules.clone();
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        try {
            Class c = findLoadedClass(name);
            if (c == null) {
                return parent.loadClass(name);
            } else {
                return c;
            }
        } catch (ClassNotFoundException ignored) {
            String path = name.replace('.', '/') + ".class";
            for (String module:modules) {
                URL url;
                try {
                    url = new URL("jar:file:" + module + "!/" + path);
                } catch (MalformedURLException e) {
                    logger.log(Level.SEVERE, "Failed to open url: " + "jar:file:" + Constant.MOD_D + path, e);
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
                    return defineClass(name,
                            classData, 0, classData.length);
                } catch (IOException e) {
                    logger.log(Level.INFO, "Failed to load: " + name + " from " + module, e);
                }
            }
        }
        logger.log(Level.SEVERE, "Failed to load class " + name);
        throw new ClassNotFoundException();
    }
}
