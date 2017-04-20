/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.http;

import java.util.Map;

public interface Response {

	String getContentType( );

	String getResponse( );

	Integer getStatus( );

	Map getHeaders( );


}