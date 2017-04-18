/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.console;

import java.lang.reflect.InvocationTargetException;

public interface Executable {
	Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException;
}
