package net.protium.core.http

import net.protium.api.events.Response
import net.protium.api.exceptions.NotFoundException
import net.protium.core.modulemanagement.Manager
import net.protium.core.utils.Constant
import net.protium.core.utils.Functions
import net.protium.core.utils.JSONParser
import net.protium.core.utils.Pair

import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * From: protium
 * Pkg: net.protium.core.http
 * At: 14.04.2017
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class Router {
    private Manager manager
    private HashMap<String, ArrayList> routes

    Router(Manager manager) {
        Logger logger = Logger.getLogger(this.getClass().getName())

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

            JSONParser parser = new JSONParser((new File(path)))
            def router = parser.get()

            router.each { item ->
                String module = item.getKey()

                def routeList = []

                item.getValue().each { pattern ->
                    routeList.add(new Pair(pattern.getKey(), pattern.getValue()))
                }

                routes.put(module, routeList)
            }
        }
    }

    Response perform(HTTPRequest target) throws NotFoundException {

        for (def item : routes) {
            def module = item.getKey()

            for (def pattern : item.getValue()) {

                def matcher = target.getURL() =~ pattern.getLeft()

                String action = pattern.getRight().get("action")
                ArrayList args = pattern.getRight().get("args") as ArrayList

                if (matcher.matches()) {

                    Map options = new HashMap()

                    Integer count = Functions.min(matcher.groupCount(), pattern.getRight().size() as Integer)

                    for (int i = 0; i < count; i++) {
                        options.put(args[i], matcher.group(i + 1))
                    }

                    target.setOptions(options)
                    target.setAction(action)

                    return manager.getModule(module).onRequest(target)
                }
            }
        }

        throw new NotFoundException()
    }
}
