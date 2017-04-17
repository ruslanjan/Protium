package net.protium.api.exceptions;

/**
 * From: protium
 * Pkg: net.protium.net.protium.api.exceptions
 * At: 14.04.2017
 */
public class InternalException extends Exception {
	private String message;

	public InternalException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage( ) {
		return  message;
	}
}
