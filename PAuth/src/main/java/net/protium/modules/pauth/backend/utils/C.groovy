/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.backend.utils

import net.protium.modules.pauth.backend.auth.PARank

/*
In net.protium.modules.pauth.utils
From temporary-protium
*/

class C {
	public static final String MODULE_NAME = 'PAuthBackend'

    public static final String DB_UNIT = "PAuth_Default"

    public static final String DB_CONF = "${MODULE_NAME}.conf/db-profiles"
	public static final String BASIC_CONF = "${MODULE_NAME}.conf/basic"
    public static final String GAPI_CONF = "${MODULE_NAME}.conf/google-api"
	public static final String SESSION_NAME = "Protium/PAuth:PAuth/Session"

	public static Integer BCRYPT_ROUNDS = 6
	public static PARank CAN_EDIT_RANKS = PARank.MOD

	public static boolean AUTH_BY_EMAIL = true
}
