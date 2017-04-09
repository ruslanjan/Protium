package net.protium.api.exceptions;

/**
 * From: groovy-ex
 * Pkg: org.nuklear.protium.core.exceptions
 * At: 06.04.2017
 */
public class ArgumentError extends CommonException {
	public ArgumentError(Class causerClass, String methodName, String argumentName, String reason) {
		super(causerClass, "in [causer]." + methodName + "():"
			+ " in argument [" + argumentName + "]:"
			+ " " + reason);
	}
}
