/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.util

/**
 * From: protium
 * Pkg: net.protium.modules.pauth.util
 * At: 15.04.17
 */
class C {
    public static String MODULE_BASENAME = "PAuth"

    public static String DB_CONF_NAME = "${MODULE_BASENAME}/database"
    public static String CRYPTO_CONF_NAME = "${MODULE_BASENAME}/crypto"
    public static String FSI_NAME = "${MODULE_BASENAME}/fsi"

    public static Integer BCRYPT_ROUNDS = 9
    public static Integer MIN_PASSWORD_LENGTH = 8
    public static Integer MAX_PASSWORD_LENGTH = 64


}
