/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.exceptions;

/**
 * From: protium
 * Pkg: net.protium.net.protium.api.exceptions
 * At: 14.04.2017
 */
public class InternalException extends Exception {
	private final String message;

	public InternalException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage( ) {
		return message;
	}
}
