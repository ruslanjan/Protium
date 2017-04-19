/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.console;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class BasicExecutable implements Executable {
	protected Method method;
	protected Object target;

	public BasicExecutable(Method method, Object target) {
		this.method = method;
		this.target = target;
	}

	public Object getTarget( ) {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Method getMethod( ) {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {

		if (args.length < method.getParameterCount()) {
			return "Too few arguments!\n";
		}

		return method.invoke(target, Arrays.copyOfRange(args, 0, method.getParameterCount()));
	}
}
