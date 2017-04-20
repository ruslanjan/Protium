/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.core

import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.objects.account.UserSettings
import net.protium.api.annotations.OnDisable
import net.protium.api.annotations.OnEnable
import net.protium.api.http.Request
import net.protium.api.module.AbstractModule
import net.protium.api.module.IModule
import net.protium.modules.pauth.database.UserManager
import net.protium.modules.pauth.database.orm.User
import net.protium.modules.pauth.http.PAResponse
import net.protium.modules.pauth.oauth2.OAuthType
import net.protium.modules.pauth.oauth2.VKIdentity
import net.protium.modules.pauth.storage.Resource

/*
In net.protium.modules.pauth.core
From temporary-protium
*/

@SuppressWarnings("GrMethodMayBeStatic")
class PAuth implements IModule {

	static UserManager manager

	@OnEnable
	void init() {

		VKIdentity.CLIENT_ID = 5991863
		VKIdentity.CLIENT_SECRET = "A6e5i9KOUL2Xythk57Fb"
		VKIdentity.AUTH_DISPLAY_TYPE = "popup"
		VKIdentity.REDIRECT_URI = "http://localhost:8081/pauth/vk-after-confirm"
		VKIdentity.SCOPES = []

		Map<String, String> options = new HashMap<>()

		options.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres")
		options.put("hibernate.connection.user", "postgres")
		options.put("hibernate.connection.password", "9827")

		manager = new UserManager(options)

		//noinspection UnnecessaryQualifiedReference
		Resource.manager = AbstractModule.moduleManager
	}

	@Override
	PAResponse onRequest(Request request) {
		try {
			println "========================================"
			println "Invoking 'action__${request.action}'"
			println "========================================"
			PAuth.invokeMethod(("action__${request.action}").trim(), [request] as Object[]) as PAResponse
		} catch (MissingMethodException ignored) {
			PAResponse response = new PAResponse()

			response.status = 404
			response.response = Resource.readFile("html/errors/no-such-action.html")

			response
		}
	}

	@SuppressWarnings("GrMethodMayBeStatic")
	static PAResponse action__vkAuth(Request request) {
		PAResponse response = new PAResponse()
		try {
			response.status = 303
			response.addHeader("Location", VKIdentity.buildAuthURI(""))
		} catch (MissingMethodException e) {
			println "FAAAAIL " + e.message
		}

		response
	}

	static PAResponse action__vkCompleteRegister(Request request) {
		String flowCode = request.query.get("code")
		UserActor actor = VKIdentity.authorize(flowCode)

		println "======================="
		println actor.accessToken
		println "======================="

		User user = new User()

		UserSettings settings = VKIdentity.apiClient
				.account().getProfileInfo(actor).execute()

		user.oauthType = OAuthType.VK
		user.oauthBind = actor.accessToken
		user.firstName = settings.firstName
		user.lastName = settings.lastName
		user.password = ""
		user.login = settings.screenName

		try {
			manager.newUser(user)
		} catch (Exception ignored) {
			PAResponse response = new PAResponse()

			response.status = 507
			response.response = "Database error"
			response.contentType = "text/plain; charset=utf-8"

			return response
		}


		PAResponse response = new PAResponse()

		response
	}

	@OnDisable
	void close() {
		manager.close()
	}
}
