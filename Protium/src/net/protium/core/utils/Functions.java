/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.utils;

import org.jetbrains.annotations.NotNull;

/**
 * From: groovy-ex
 * Pkg: org.nuklear.protium.core.utils
 * At: 05.04.2017
 */
public class Functions {

	@NotNull
	static public String implode(String[] array, String glue) {
		StringBuilder builder = new StringBuilder();

		for (String current :
			array) {
			builder
				.append(current)
				.append(glue);
		}

		String result = builder.toString();

		return result.substring(0, result.length() - glue.length());
	}

}
