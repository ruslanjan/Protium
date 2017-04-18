/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.util

import net.protium.api.agents.Config

/**
 * From: protium
 * Pkg: net.protium.modules.pauth.util
 * At: 16.04.17
 */
final class FirstStart {

    static void makeConfigs() {

        if(isFirstRun())
            return

        makeFSI();
        makeDBConf();
        makeCryptoConf();
    }

    private static void makeCryptoConf() throws IOException {
        if (Config.createConfig(C.CRYPTO_CONF_NAME)) {

            Config cc = new Config(C.CRYPTO_CONF_NAME)

            cc
                    .set([:])
                    .set("bcrypt", [:])
                    .set("password", [:])
                    .set("bcrypt.saltStrength", C.BCRYPT_ROUNDS)
                    .set("password.min", C.MIN_PASSWORD_LENGTH)
                    .set("password.max", C.MAX_PASSWORD_LENGTH)
                    .commit()

        } else {
            throw new IOException("Couldn't create '${C.CRYPTO_CONF_NAME}' config!");
        }
    }

    private static void makeDBConf() throws IOException {
        if (Config.createConfig(C.DB_CONF_NAME)) {

            Config cc = new Config(C.DB_CONF_NAME)

            cc
                    .set([:])
                    .set("profile", "")
                    .set("profiles", [:])
                    .commit()
        } else {
            throw new IOException("Couldn't create '${C.DB_CONF_NAME}' config!");
        }
    }

    private static void makeFSI() {
        if (Config.createConfig(C.FSI_NAME)) {

            Config cc = new Config(C.FSI_NAME)

            cc
                    .set("pre-db-conf")
                    .commit()

        } else {
            throw new IOException("Couldn't create FSI!");
        }
    }

    static String getStatus() {
        if(Config.configExists(C.FSI_NAME)) {
            (new Config(C.FSI_NAME)).get()
        } else {
            "pre-conf"
        }
    }
}
