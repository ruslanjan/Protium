/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.utils.jsonschema

import groovy.json.JsonSlurper

class JSONSchemaResolver {

    private static Map<String, Object> loadedSchemas = new HashMap<String, Object>()


    static Object resolveSchema(uri, parentSchema = null) {
        uri = resolveUri(uri, parentSchema)
        if (!loadedSchemas.containsKey(uri)) {
            tryToLoad(uri)
        }
        return loadedSchemas.get(uri)
    }

    protected static String resolveUri(uri, parent) {
        if (!parent) {
            return uri
        }
        def baseUri = parent.resolvedId
        if (!baseUri) {
            baseUri = parent.id
        }
        return resolveRelativeUri(uri, baseUri)
    }


    protected static void tryToLoad(String uri) {
        InputStream input
        if (uri ==~ /^classpath:\/\/.*/) {
            input = JSONSchemaResolver.class.getResourceAsStream(uri.substring(12))
        } else if (uri ==~ /^file:\/\/.*/) {
            input = new FileInputStream(uri.substring(7))
        } else if (uri ==~ /^http(?:s)?:\/\/.*/) {
            input = new URL(uri).openStream()
        } else {
            return
        }

        try {
            JsonSlurper sluper = new JsonSlurper()
            Object schema = sluper.parse(new InputStreamReader(input))
            if (schema) {
                schema.resolvedId = uri
                loadedSchemas.put(uri, schema)
            }
        } catch (Exception ignored) {
        }
    }

    private static String resolveRelativeUri(String uri, String base) {
        if (!base || isAbsolute(uri)) {
            return uri
        }
        if (base.endsWith("/")) {
            return base + uri
        }
        return base.substring(0, base.lastIndexOf("/") + 1) + uri
    }

    private static boolean isAbsolute(uri) {
        return uri ==~ /^.*:\/\/.*/
    }
}
