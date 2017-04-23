/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.oauth2

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.UserAuthResponse
import net.protium.api.utils.Functions
/*
In net.protium.modules.pauth.oauth2
From temporary-protium
*/

class VKIdentity {
	public static final String API_VERSION = "5.63"
	public static Integer CLIENT_ID = null
	public static String CLIENT_SECRET = null
	public static String REDIRECT_URI = null
	public static String AUTH_DISPLAY_TYPE = "page"
	public static String[] SCOPES = []

	static VkApiClient apiClient = new VkApiClient(new HttpTransportClient())

	static String buildAuthURI(String comment) {
		StringBuilder url = new StringBuilder("https://oauth.vk.com/authorize?")

        Map<String, String> options = new HashMap<>()

		options.put("client_id", CLIENT_ID.toString())
		options.put("redirect_uri", REDIRECT_URI)
		options.put("display", AUTH_DISPLAY_TYPE.toString())
		options.put("scopes", Functions.implode(
				SCOPES, ","))
		options.put("response_type", "code")
		options.put("v", API_VERSION)
		options.put("state", comment)

		options.entrySet().forEach { entry ->
			url.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&")
		}

		return url.toString()
    }

	static UserActor authorize(String flowCode) {
		UserAuthResponse response = apiClient.oauth()
				.userAuthorizationCodeFlow(
				CLIENT_ID, CLIENT_SECRET,
				REDIRECT_URI, flowCode)
				.execute()

		return new UserActor(response.userId, response.accessToken)
	}


}
