/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pte.basic

import freemarker.cache.MruCacheStorage
import freemarker.cache.StringTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.Version
import net.protium.modules.pte.core.PTemplate
import net.protium.modules.pte.core.PTemplatizer

/*
In net.protium.modules.pte.basic
From temporary-protium
*/

class PBasicTemplatizer implements PTemplatizer {

	static public Integer CACHE_SOFT_LIMIT = 250
	static public Integer CACHE_HARD_LIMIT = 25
	static public final Integer[] FTL_VERSION = [2, 3, 26]

	protected Configuration cfg
	protected StringTemplateLoader templateLoader

	PBasicTemplatizer() {
		cfg = new Configuration(new Version(FTL_VERSION[0], FTL_VERSION[1], FTL_VERSION[2]))
		cfg.cacheStorage = new MruCacheStorage(CACHE_HARD_LIMIT, CACHE_SOFT_LIMIT)
		cfg.templateLoader = templateLoader = new StringTemplateLoader()
	}

	@Override
	PBasicTemplatizer register(String name, data) {
		URL url = data
		data = url.text
		templateLoader.putTemplate(name, data as String)

		this
	}

	@Override
	PBasicTemplatizer register(Map<String, ?> list) {
		list.each { String key, value ->
			register(key, value)
		}

		this
	}

	@Override
	PBasicTemplate get(String name) {
		new PBasicTemplate(cfg.getTemplate(name))
	}

	protected class PBasicTemplate implements PTemplate {

		Template template
		Map<String, Object> bindings = new HashMap<>()
		String processed = null

		PBasicTemplate(Template template) {
			this.template = template
		}

		@Override
		PBasicTemplate bind(String name, Object data) {
			bindings.put(name, data)

			this
		}

		@Override
		PBasicTemplate bind(Map<String, ?> data) {
			bindings.putAll(data)

			this
		}

		@Override
		PBasicTemplate execute() {
			StringWriter writer = new StringWriter()

			template.process(bindings, writer)

			processed = writer.toString()

			this
		}

		@Override
		String toString() {
			processed
		}
	}

}


