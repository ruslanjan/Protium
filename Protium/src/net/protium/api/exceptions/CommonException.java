/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.exceptions;

/**
 * From: groovy-ex
 * Pkg: org.nuklear.protium.core.exceptions
 * At: 06.04.2017
 */
public class CommonException extends Exception {

	private String causer, message;

	CommonException(Class causerClass, String reason) {
		message = reason;
		causer = causerClass.getName();
	}

	public String getMessage( ) {
		return "Thrown E[" +
			this.getClass().getName() +
			"]\n\t" +
			"Causer: [" + causer +
			"]\n\t" +
			"Reason: " + message;
	}

	public String toString( ) {
		return "E[" +
			this.getClass().getName() +
			"]";
	}
}
