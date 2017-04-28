/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.utils

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonSchema
import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.protium.api.exceptions.ArgumentException
import net.protium.api.utils.jsonschema.JsonSchemaHelper

class AbstractJSONParser {

	protected def data
	protected File file

	static String PATH_SEPARATOR = '.'
	static String PATH_SEPARATOR_REGEX = '\\.'

	static String INDEX_FIND_REGEX = /\[[0-9]+?]/
	static String INDEX_EXTRACT_REGEX = /[0-9]+/

	static def openFile(File file) throws JsonException {
		(new JsonSlurper()).parse(file)
	}

	static String toPath(String[] arrayPath) {
		Functions.implode(arrayPath, PATH_SEPARATOR)
	}

	def get(String propertyPath) {
		String[] path = propertyPath.split(PATH_SEPARATOR_REGEX)

		def current = data

		while (1) {
			String siblingName = path[0]
			path = path.length > 1 ? path[1..-1] : []

			if (siblingName.matches(INDEX_FIND_REGEX)) {
				siblingName = (siblingName =~ INDEX_EXTRACT_REGEX)[0]
				current = (current as ArrayList)[siblingName as int]
			} else {
				current = current[siblingName]
			}

			if (path.length > 0 && current == null) {
				throw new ArgumentException()
			}

			if (path.length == 0) {
				break
			}
		}

		current
	}

	Set validate(String schemaName) {
		try {
			File file1 = new File(Functions.pathToFile(Constant.SCHEMA_DIR, schemaName, Constant.SCHEMA_EXT))

			JsonSchema schema = JsonSchemaHelper.getJsonSchemaFromUrl(file1.toURI().toURL())

			JsonNode node = JsonSchemaHelper.getJsonNodeFromUrl(file.toURI().toURL())

			Set errors = schema.validate(node)


			return errors
		} catch (Exception ignored) {
			new HashSet()
		}
	}

	boolean checkPath(String propertyPath) {
		String[] path = propertyPath.split(PATH_SEPARATOR_REGEX)

		def current = data

		while (1) {
			String siblingName = path[0]
			path = path.length > 1 ? path[1..-1] : []

			if (siblingName.matches(INDEX_FIND_REGEX)) {
				siblingName = (siblingName =~ INDEX_EXTRACT_REGEX)[0]
				current = (current as ArrayList)[siblingName as int]
			} else {
				current = current[siblingName]
			}

			if (path.length > 0 && current == null) {
				return false
			}

			if (path.length == 0) {
				break
			}
		}

		current != null
	}

	HashMap getMap(String path) {
		get(path) as HashMap
	}

	String getString(String path) {
		get(path) as String
	}

	ArrayList getArrayList(String path) {
		get(path) as ArrayList
	}

	Integer getInteger(String path) {
		get(path) as Integer
	}

	boolean getBoolean(String path) {
		get(path) as boolean
	}

	def get() { data }

	AbstractJSONParser set(String path, value) {
		data = setRecursive(data, path.split(PATH_SEPARATOR_REGEX), value)
		this
	}

	AbstractJSONParser set(value) {
		data = value
		this
	}

	protected def setRecursive(object, String[] path, value) {
		if (path.length > 0) {
			if (path[0].matches(INDEX_FIND_REGEX)) {
				def siblingName = (siblingName =~ INDEX_EXTRACT_REGEX)[0]
				object = object as ArrayList
				if (object == null) {
					object = new ArrayList()
					object.set(siblingName as Integer, setRecursive(object.get(siblingName as int), (path.length > 1 ? path[1..-1] : []) as String[], value))
					return object
				}
			}
			object = object as Map
			if (object == null) {
				object = new HashMap<Object, Object>()
			}
			object.put(path[0], setRecursive(object.get(path[0]), (path.length > 1 ? path[1..-1] : []) as String[], value))

			object
		} else {
			value
		}
	}

	AbstractJSONParser commit(boolean prettyPrint = true) {
		if (file != null) {
			FileWriter writer = new FileWriter(file, false)
			writer.write(
					prettyPrint ?
							JsonOutput.prettyPrint(JsonOutput.toJson(data)) :
							JsonOutput.toJson(data)
			)
			writer.close()
		}

		this
	}
}
