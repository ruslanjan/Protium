/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.exceptions;

public class ArgumentError extends CommonException {
	public ArgumentError(Class causerClass, String methodName, String argumentName, String reason) {
		super(causerClass, "in [causer]." + methodName + "():"
			+ " in argument [" + argumentName + "]:"
			+ " " + reason);
	}
}
