/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.event;

import java.util.Map;

public interface Request {
	String getMethod( );

	String setRawData( String data );
	String getRawData( );

	Object getSpecialData( );

	Object setHeaders( Map headers );
	Object getHeaders( );

	void setAction( String action );
	String getAction();
}
