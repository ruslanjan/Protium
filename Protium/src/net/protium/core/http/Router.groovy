package net.protium.core.http

import groovy.json.JsonException

import net.protium.api.events.Response
import net.protium.api.exceptions.InternalException
import net.protium.api.exceptions.NotFoundException
import net.protium.core.modulemanagement.Manager
import net.protium.core.utils.Constant
import net.protium.core.utils.Functions
import net.protium.core.utils.JSONParser
import net.protium.core.utils.Pair

import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Matcher

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
                    Functions.createFile(Constant.LOG_D, this.getClass().getName(), Constant.LOG_EXT))))
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write logs", e)
        }

        this.manager = manager

        reload()
    }

    def reload() {
        routes = new HashMap<>()
        String[] paths = Functions.listFiles(Constant.ROUTES_D, Constant.CONF_EXT)

        for (String path : paths) {

            JSONParser parser

            try {
                parser = new JSONParser((new File(path)))
            } catch (JsonException e) {
                logger.log(Level.SEVERE, "failed to read route file '$path'", e)
                continue
            }

            def router = parser.get()

            if (!(router instanceof Map)) {
                logger.severe("Invalid schema in route file '$path'")
                continue
            }

            try {
                router.each { item ->
                    String module = item.getKey()

                    def routeList = routes.get(module) ?: []


                    if (!(item.getValue() instanceof Map)) {
                        throw new InternalException("wrongschema")
                    }

                    item.getValue().each { pattern ->

                        if ((!(pattern.getValue() instanceof Map)) ||
                                (!(pattern.getValue().get("args") instanceof List)) ||
                                (!(pattern.getValue().get("action") instanceof String))
                        ) {
                            throw new InternalException("wrongschema")
                        }

                        routeList.add(new Pair(pattern.getKey(), pattern.getValue()))
                    }

                    routes.put(module, routeList)
                }
            } catch (InternalException ignored) {
                logger.severe("Invalid schema in route file '$path'")
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
