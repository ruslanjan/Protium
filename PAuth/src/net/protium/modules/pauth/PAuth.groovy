/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth

import net.protium.api.agents.Config
import net.protium.api.agents.CoreAgent
import net.protium.api.events.Request
import net.protium.api.module.Module
import net.protium.modules.pauth.database.SqlWrapper
import net.protium.modules.pauth.request.PAResponse
import net.protium.modules.pauth.util.C

import java.util.logging.Logger
/**
 * From: protium
 * Pkg: net.protium.modules.pauth
 * At: 15.04.17
 */
class PAuth extends Module {

    PAuth() {

    }

    SqlWrapper sql
    Logger logger = Logger.getLogger(getClass().getName())
    Config cryptoConf

    @Override
    void onEnable() {

//        if(FirstStart.getStatus() == "pre-conf") {
//            FirstStart.makeConfigs()
//
//            logger.info("")
//        }
//
//        logger.addHandler(new FileHandler(
//                Functions.createFile("/home/kkremen/logs", getClass().getName(), ".log.xml")
//        ))
    }

    @Override
    PAResponse onRequest(Request request) {
        PAResponse par = new PAResponse()
        URL url = CoreAgent.moduleManager.getModuleResourceURL(C.MODULE_BASENAME, "res/data.txt")

        String file = url.getText()

        par.response = "URL response: " + file

        par
    }

    @Override
    void onDisable() {
        cryptoConf.commit(true)
    }
}
