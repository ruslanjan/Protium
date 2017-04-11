/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.events;

public interface Response {

	String getContentType();
	void setContentType(String type);

	String getResponse();
	void setResponse(String response);

}