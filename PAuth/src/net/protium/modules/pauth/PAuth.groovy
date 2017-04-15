/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth

import net.protium.api.agents.Config
import net.protium.api.agents.Functions
import net.protium.api.events.Request
import net.protium.api.module.Module
import net.protium.modules.pauth.database.SqlWrapper
import net.protium.modules.pauth.request.Actions
import net.protium.modules.pauth.request.PAResponse
import net.protium.modules.pauth.util.C

import java.util.logging.FileHandler
import java.util.logging.Logger

/**
 * From: protium
 * Pkg: net.protium.modules.pauth
 * At: 15.04.17
 */
class PAuth implements Module {
    SqlWrapper sql
    Logger logger = Logger.getLogger(getClass().getName())
    Config cryptoConf

    @Override
    void onEnable() {

        logger.addHandler(new FileHandler(
                Functions.createFile("/home/kkremen/logs", getClass().getName(), ".log.xml")
        ))

        println Config.createConfig(C.DB_CONF_NAME)
        println Config.createConfig(C.CRYPTO_CONF_NAME)

        sql = new SqlWrapper()

        cryptoConf = new Config(C.CRYPTO_CONF_NAME)

        C.BCRYPT_ROUNDS = cryptoConf.checkPath("bcrypt.saltStrength") ?
                cryptoConf.getInteger("bcrypt.salt.rounds") :
                C.BCRYPT_ROUNDS

        C.MIN_PASSWORD_LENGTH = cryptoConf.checkPath("bcrypt.saltStrength") ?
                cryptoConf.getInteger("bcrypt.password.minLength") :
                C.MIN_PASSWORD_LENGTH

        C.MAX_PASSWORD_LENGTH = cryptoConf.checkPath("bcrypt.saltStrength") ?
                cryptoConf.getInteger("bcrypt.password.maxLength") :
                C.MAX_PASSWORD_LENGTH

        cryptoConf.set("bcrypt.saltStrength", C.BCRYPT_ROUNDS)
        cryptoConf.set("bcrypt.password.minLength", C.MIN_PASSWORD_LENGTH)
        cryptoConf.set("bcrypt.password.maxLength", C.MAX_PASSWORD_LENGTH)
    }

    @Override
    PAResponse onRequest(Request request) {
        Actions.invokeMethod("action\$${request.getAction()}", [request]) as PAResponse
    }

    @Override
    void onDisable() {
        cryptoConf.commit(true)
    }
}
