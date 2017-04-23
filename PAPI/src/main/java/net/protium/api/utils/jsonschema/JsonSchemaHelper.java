/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.utils.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;

import java.io.InputStream;
import java.net.URL;

/*
In ${PACKAGE_NAME}
From temporary-protium
*/
public class JsonSchemaHelper {
	public JsonNode getJsonNodeFromClasspath(String name) throws Exception {
		InputStream is1 = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(name);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(is1);
	}

	public JsonNode getJsonNodeFromStringContent(String content) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(content);
	}

	public JsonNode getJsonNodeFromUrl(URL url) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(url);
	}

	public JsonSchema getJsonSchemaFromClasspath(String name) throws Exception {
		JsonSchemaFactory factory = new JsonSchemaFactory();
		InputStream is = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(name);
		return factory.getSchema(is);
	}

	public JsonSchema getJsonSchemaFromStringContent(String schemaContent) throws Exception {
		JsonSchemaFactory factory = new JsonSchemaFactory();
		return factory.getSchema(schemaContent);
	}

	public JsonSchema getJsonSchemaFromUrl(URL url) throws Exception {
		JsonSchemaFactory factory = new JsonSchemaFactory();
		return factory.getSchema(url);
	}

	public JsonSchema getJsonSchemaFromJsonNode(JsonNode jsonNode) throws Exception {
		JsonSchemaFactory factory = new JsonSchemaFactory();
		return factory.getSchema(jsonNode);
	}
}