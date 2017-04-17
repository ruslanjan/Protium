/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.events;

import java.util.HashMap;
import java.util.Map;

public interface Request {
	String getMethod();

	String getRawData();

	Object getSpecialData();

	Object getHeaders();

	String getAction();

	Map getOptions();

	String getURL();

	String getRawQueryString();

	Map getQuery();
}
