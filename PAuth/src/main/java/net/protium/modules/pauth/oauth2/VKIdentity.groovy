/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.oauth2
/*
In net.protium.modules.pauth.oauth2
From temporary-protium
*/

class VKIdentity {

    enum DisplayType {
        POPUP, PAGE, MOBILE
    }

    static String makeAuthUrl(String clientID, String redirectURL, D) {
        //noinspection GroovyUnusedAssignment
        String url = "https://oauth.vk.com/authorize"

        Map<String, String> options = new HashMap<>()

        options.put("client_id", clientID)
        options.put("redirect_uri", redirectURL)
        options.put("display", displayType)
        options.put("client_id", clientID)
    }

}
