/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.console;

import java.util.List;

public class Command {
	private String name, raw;
	private List args;

	Command( ) {
	}

	Command(String name, String raw, List args) {
		this.name = name;
		this.raw = raw;
		this.args = args;
	}

	public String getRaw( ) {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public List getArgs( ) {
		return args;
	}

	public void setArgs(List args) {
		this.args = args;
	}

	public String getName( ) {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
