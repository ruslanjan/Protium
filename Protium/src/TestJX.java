/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import net.protium.core.utils.JSONParser;

import java.io.File;
import java.util.HashMap;

/**
 * From: protium
 * Pkg: PACKAGE_NAME
 * At: 13.04.17
 */
public class TestJX {
	public static void main(String[] args) {
		JSONParser parser = new JSONParser(new File("data/config/database.json"));

		HashMap map = (HashMap) parser.get();

		System.out.println();
	}
}
