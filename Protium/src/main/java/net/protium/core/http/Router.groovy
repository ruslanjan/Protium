/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http

import groovy.json.JsonException
import net.protium.api.events.Response
import net.protium.api.exceptions.NotFoundException
import net.protium.api.utils.Constant
import net.protium.api.utils.Functions
import net.protium.api.utils.JSONParser
import net.protium.api.utils.Pair
import net.protium.core.modules.management.Manager

import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.PatternSyntaxException

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 14.04.2017
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class Router extends AbstractRouter {
    private Map routes
    private Logger logger

    Router(Manager manager) {
        logger = Logger.getLogger(this.getClass().getName())

        try {
            logger.addHandler((new FileHandler(
                    Functions.createFile(Constant.LOG_DIR, this.getClass().getName(), Constant.LOG_EXT))))
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write logs", e)
        }

        this.manager = manager

        reload()
    }

    def reload() {
        routes = new HashMap<>()
        String[] paths = Functions.listFiles(Constant.ROUTES_DIR, Constant.CONF_EXT)

        for (String path : paths) {

            JSONParser parser

            try {
                parser = new JSONParser(new File(path), "route")
            } catch (JsonException e) {
                logger.log(Level.WARNING, "failed to read route file '$path'", e)
                continue
            }

            def router = parser.get()

            router.each { item ->
                String module = item.getKey()

                def routeList = routes.get(module) ?: []

                item.getValue().each { pattern ->

                    boolean isValid = true

                    try {
                        "" =~ pattern.getKey()
                    } catch (PatternSyntaxException ignored) {
                        logger.log(Level.WARNING, "Couldn't parse regex '%${pattern.getKey()}%' in file '$path'. Skipped it.")
                        isValid = false
                    }

                    if (isValid) {
                        routeList.add(new Pair(pattern.getKey(), pattern.getValue()))
                    }
                }
                routes.put(module, routeList)
            }
        }
    }

    Response perform(HTTPRequest target) throws NotFoundException {
        for (def item : routes) {
            String module = item.getKey()

            for (def pattern : item.getValue()) {

                String action
                ArrayList args

                Matcher matcher = Functions.getMatcher(pattern.getLeft(), target.getURL())

                try {
                    action = (pattern.getRight()).get("action")
                    args = (pattern.getRight()).get("args") as ArrayList
                } catch (MissingMethodException e) {
                    logger.log(Level.SEVERE, "Invalid schema in some of route files!", e)
                    continue
                }

                if (matcher.matches()) {

                    Map options = new HashMap()

                    Integer count = Functions.min(matcher.groupCount(), pattern.getRight().size() as Integer)

                    for (int i = 0; i < count; i++) {
                        options.put((args as ArrayList)[i], matcher.group(i + 1))
                    }

                    target.setOptions(options)
                    target.setAction(action)

                    return _perform(module, target)
                }
            }
        }

        throw new NotFoundException()
    }
}
