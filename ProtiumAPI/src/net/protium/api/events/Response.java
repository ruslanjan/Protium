/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.events;

public interface Response {

	String getContentType();

	String getResponse();

	Integer getStatus();

}