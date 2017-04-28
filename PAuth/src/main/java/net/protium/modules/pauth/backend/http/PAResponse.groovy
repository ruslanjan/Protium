/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.backend.http

import net.protium.api.http.HTTPResponse
import net.protium.api.http.Response

/*
In net.protium.modules.pauth.http
From temporary-protium
*/

class PAResponse extends HTTPResponse implements Response {
	Integer status = 200
	String response = ""

	void addHeader(String name, value) {
		headers.put(name, value)
	}
}
