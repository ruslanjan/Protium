/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.http;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface Request {
	String getMethod( );

	HttpSession getSession( );

	String getRawData( );

	Object getSpecialData( );

	Object getHeaders( );

	String getAction( );

	Map getOptions( );

	String getURL( );

	String getRawQueryString( );

	Map getQuery( );
}
