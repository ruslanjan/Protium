/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.console;

import java.util.HashMap;
import java.util.Map;

public class BasicCommandList implements CommandList {

	protected final Map < String, Executable > methodMap = new HashMap <>();

	@Override
	public Executable get(String command) {
		return methodMap.get(command);
	}

	public void register(@SuppressWarnings("SameParameterValue") String name, Executable command) {
		methodMap.put(name, command);
	}
}
