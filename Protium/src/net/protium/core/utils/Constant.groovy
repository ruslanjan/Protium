/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.utils

@SuppressWarnings("GroovyUnusedDeclaration")
class Constant {


    public static String VERSION = "v0.0.0-dev"

    public static String HOME_DIR = "."

    public static String CONF_DIR = "config"
    public static String MOD_DIR = "modules"
    public static String LOG_DIR = "logs"
    public static String ROUTES_DIR = "routes"
    public static String RES_DIR = "resources"
    public static String DATA_DIR = "data"

    public static String MOD_EXT = ".jar"
    public static String CONF_EXT = ".json"
    public static String LOG_EXT = ".log.xml"

    public static String SERVER_TEXT = "\n" +
            "<b>Software</b>: Protium/Jetty v9.4.3\n" +
            "<b>OS</b>: ${System.getProperty('os.name')} ${System.getProperty('os.version')} ${System.getProperty('os.arch')} \n" +
            "<b>JVM</b>: ${System.getProperty('java.vm.name')} ${System.getProperty('java.version')}/${System.getProperty('java.vm.version')} by ${System.getProperty('java.vendor')}\n" +
            "Running on Protium ${VERSION}"

    public static String HTML_CONTENT_TYPE = "text/html; charset=utf-8"
}
