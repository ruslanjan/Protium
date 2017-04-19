/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.utils.jsonschema

class JSONSchema {

    static boolean conformsSchema(Object instance, Object schema = null) {
        schema = schema ?: instance.getSchema()

        if (!schema) {
            return true
        }

        if (schema instanceof String) {
            schema = resolve(schema)
        }

        if (schema.$ref) {
            return conformsSchema(instance, JSONSchemaResolver.resolveSchema(schema.$ref, schema.getParent()))
        }

        if (isNull(instance)) {
            return !schema.required
        }

        def types = schema.type instanceof List ? schema.type : [schema.type]

        boolean result = false

        for (def type : types) {
            if (type == 'array') {
                result = result || validateArray(instance, schema)
            }
            if (type == 'string') {
                result = result || isString(instance)
            }
            if (type == 'number') {
                result = result || isNumber(instance)
            }
            if (type == 'integer') {
                result = result || isInteger(instance)
            }
            if (type == 'boolean') {
                result = result || isBoolean(instance)
            }
            if (type == 'null') {
                result = result || instance == null
            }

            if (type == 'object' || schema.properties != null) {
                result = result || isObject(instance) &&
                        schema.properties.every { name, property ->
                            def value = instance."$name"
                            setParentIfNotNull(property, schema)
                            return conformsSchema(value, property)
                        }
            }

        }

        result
    }

    static boolean validateArray(Object value, Object schema) {
        return isArray(value) && value.every { item ->
            def items = schema.items
            setParentIfNotNull(items, schema)
            return conformsSchema(item, items)
        }
    }


    static void setSchema(Object obj, Object jsonSchema) {
        obj.getMetaClass().getSchema = { -> jsonSchema }
    }

    static boolean isString(value) {
        return value == null || value instanceof String
    }

    static boolean isNumber(value) {
        return value == null || value instanceof Number
    }

    static boolean isInteger(value) {
        return value == null || value instanceof Integer
    }

    static boolean isBoolean(value) {
        return value == null || value instanceof Boolean
    }

    static boolean isArray(value) {
        return value == null || value.getClass().isArray() || value instanceof List
    }

    static boolean isObject(value) {
        return value == null || (!isString(value) && !isNull(value) && !isInteger(value) && !isBoolean(value) && !isArray(value))
    }

    static boolean isNull(value) {
        return value == null
    }

    static Object resolve(String id) {
        return JSONSchemaResolver.resolveSchema(id)
    }

    private static setParentIfNotNull(schema, parent) {
        if (schema) {
            schema.getMetaClass().getParent = { -> parent }
        }
    }
}
