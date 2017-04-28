/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.frontend.view

import net.protium.api.agents.Config
import net.protium.modules.pauth.frontend.util.Storage
import net.sf.jmimemagic.Magic
import net.sf.jmimemagic.MagicMatch

import java.nio.charset.Charset

/*
In net.protium.modules.pauth.frontend.view
From temporary-protium
*/

final class Template {

	private String contents, mimeType
	private Map bindings

	Template(String fileName) {
		contents = Storage.readFile(fileName)
		bindings = new HashMap<String, String>()

		MagicMatch magic = Magic.getMagicMatch(contents.getBytes(Charset.defaultCharset()))
		mimeType = magic.getMimeType()
	}

	Template bind(Config config) {
		bindings.putAll(config.getMap("bindings"))
		this
	}

	Template bind(Map map) {
		bindings.putAll(map)
		this
	}

	Template bind(String name, String value) {
		bindings.put(name, value)
		this
	}

	Template processRegex(String regex, String replacement) {
		contents = contents.replaceAll(regex, replacement)
		this
	}

	Template exec() {
		bindings.each { key, value ->
			contents = contents.replaceAll(/#{${key}}/.toString(), value as String)
		}
		this
	}

	String toMimeType() {
		mimeType
	}

	String toString() {
		contents
	}
}
