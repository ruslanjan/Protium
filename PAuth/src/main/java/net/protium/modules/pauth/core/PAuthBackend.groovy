/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.core

import groovy.json.JsonOutput
import net.protium.api.agents.Config
import net.protium.api.annotations.OnDisable
import net.protium.api.annotations.OnEnable
import net.protium.api.exceptions.ConfigException
import net.protium.api.http.Request
import net.protium.api.module.AbstractModule
import net.protium.api.module.IModule
import net.protium.modules.pauth.auth.PARank
import net.protium.modules.pauth.auth.PASession
import net.protium.modules.pauth.database.UserManager
import net.protium.modules.pauth.database.orm.PAUser
import net.protium.modules.pauth.http.PAResponse
import net.protium.modules.pauth.utils.*

import javax.servlet.http.HttpSession

/*
In net.protium.modules.pauth.core
From temporary-protium
*/

@SuppressWarnings("GrMethodMayBeStatic")
class PAuthBackend implements IModule {

	// region Fields
	static UserManager manager
	static Config basicConf
	// endregion

	// region Constructors
	PAuthBackend() {
	}
	// endregion

	// region Protium API
	@OnEnable
	void init() {
		Storage.manager = AbstractModule.moduleManager

		String[] configs = [C.DB_CONF, C.BASIC_CONF]

		configs.each { item ->
			if (!Config.configExists(item) && !Config.createConfig(item, "{}")) {
				Storage.manager.setModuleViewStatus(C.MODULE_NAME, "CFG_ERR")
				throw new ConfigException()
			}
		}

		basicConf = new Config(C.BASIC_CONF)
		//region Basic config reading
		C.AUTH_BY_EMAIL = basicConf.checkPath("auth.byEmail") ?
				basicConf.getBoolean("auth.byEmail") :
				C.AUTH_BY_EMAIL
		basicConf.set("auth.byEmail", C.AUTH_BY_EMAIL)
		//endregion
		basicConf.commit()

		manager = new UserManager()
	}

	@Override
	PAResponse onRequest(Request request) {
		try {
			PAuthBackend.invokeMethod(("action\$${request.action}").trim(), [request] as Object[]) as PAResponse
		} catch (MissingMethodException ignored) {
			PAResponse response = new PAResponse()

			response.status = 404
			response.response = Storage.readFile("html/errors/no-such-action.html")

			response
		}
	}

	@OnDisable
	void close() {
		manager.close()
	}
	// endregion

	// region Debug
	@Debug
	@Action
	private static PAResponse action$viewRawUser(Request request) {

		PAResponse response = new PAResponse()

		Integer id = request.options.get("id") as Integer

		response.response = manager.getById(id).toString()
		response.contentType = "text/plain; charset=utf-8"

		response
	}

	@Debug
	@Action
	private static PAResponse action$showSession(Request request) {
		HttpSession session = request.session

		StringBuilder builder = new StringBuilder("Session #${session.hashCode()}: {\n")

		session.attributeNames.each { name ->
			builder.append("\t ${name} = ${session.getAttribute(name)},\n")
		}

		PAResponse response = new PAResponse()

		response.response = builder.toString() + "}"
		response.contentType = "text/plain; charset=utf-8"

		response
	}
	// endregion

	// region Regular API
	@API
	boolean checkPermission(HttpSession session, PARank needed) {
		PASession sessionData = new PASession(session.getAttribute(C.SESSION_NAME) as String)
		PARank rank

		if (!manager.verifySession(sessionData)) {
			rank = PARank.GST
		} else {
			PAUser user = manager.getById(sessionData.userId)
			rank = user.rank ?: PARank.GST
		}

		rank.satisfies(needed)
	}

	@API
	PARank getRank(HttpSession session) {
		PASession sessionData = new PASession(session.getAttribute(C.SESSION_NAME) as String)
		PARank rank

		if (!manager.verifySession(sessionData)) {
			rank = PARank.GST
		} else {
			PAUser user = manager.getById(sessionData.userId)
			rank = user.rank ?: PARank.GST
		}

		rank
	}
	// endregion

	// region Actions
	@Action
	private static PAResponse action$performAuth(Request request) {
		Map data = request.specialData as Map

		String login = data.get("login")
		String password = data.get("password")

		Integer userId

		try {
			if (C.AUTH_BY_EMAIL) {
				userId = manager.getIdByAuthOrEmail(login, password)
			} else {
				userId = manager.getIdByAuth(login, password)
			}
		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		if (userId == null) {
			Object object = [
					status: "error",
					code  : 2,
					reason: "Incorrect login or password!"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		String session
		try {
			session = manager.newSession(userId)
		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		Object object = [
				status  : "success",
				response: null
		]

		request.session.setAttribute(C.SESSION_NAME, session)

		PAResponse response = new PAResponse()

		response.response = JsonOutput.toJson(object)
		response.contentType = "application/json; charset=utf-8"

		response // <-
	}

	@Action
	private static PAResponse action$checkSession(Request request) {
		boolean result

		try {
			PASession session = new PASession(request.session.getAttribute(C.SESSION_NAME) as String)
			result = manager.verifySession(session)
		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		Object object = [
				status  : "success",
				response: result
		]

		PAResponse response = new PAResponse()

		response.response = JsonOutput.toJson(object)
		response.contentType = "application/json; charset=utf-8"

		response // <-
	}

	@Action
	private static PAResponse action$performLogout(Request request) {
		try {
			request.session.removeAttribute(C.SESSION_NAME)
		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		PAResponse response = new PAResponse()

		Object object = [
				status  : "success",
				response: null
		]

		response.response = JsonOutput.toJson(object)
		response.contentType = "application/json; charset=utf-8"

		response // <-
	}

	@Action
	private static PAResponse action$userExists(Request request) {
		PAUser user

		try {
			String login = (request.specialData as Map).get("login")
			String email = (request.specialData as Map).get("email")

			user = manager.getByUnique(login, email)
		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		boolean result = user != null

		PAResponse response = new PAResponse()

		Object object = [
				status  : "success",
				response: result
		]

		response.response = JsonOutput.toJson(object)
		response.contentType = "application/json; charset=utf-8"

		response // <-
	}

	@Action
	private static PAResponse action$performRegister(Request request) {

		PAUser user

		try {
			Map options = request.specialData as Map

			String login = options.get("login")
			String password = options.get("password")
			String email = options.get("email")
			String firstName = options.get("first-name")
			String middleName = options.get("middle-name")
			String lastName = options.get("last-name")
			PARank rank = PARank.USR

			if (manager.getByUnique(login, email) != null) {
				Object object = [
						status: "error",
						code  : 3,
						reason: "User with such login/email already exists!"
				]

				PAResponse response = new PAResponse()

				response.response = JsonOutput.toJson(object)
				response.contentType = "application/json; charset=utf-8"

				return response // <-
			}

			user = new PAUser(
					login, email, password,
					firstName, middleName, lastName,
					rank
			)

			manager.newUser(user)
		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		Object object = [
				status    : "success",
				"response": null
		]

		PAResponse response = new PAResponse()

		response.response = JsonOutput.toJson(object)
		response.contentType = "application/json; charset=utf-8"

		return response // <-

	}

	@Action
	private static PAResponse action$alterUser(Request request) {

		PAUser user

		try {
			Map options = request.specialData as Map

			Integer id = Integer.valueOf(options.get("user_id") as String)
			String login = options.get("login")
			String password = options.get("password")
			String email = options.get("email")
			String firstName = options.get("first-name")
			String middleName = options.get("middle-name")
			String lastName = options.get("last-name")

			if (manager.getByUnique(login, email) != null) {
				Object object = [
						status: "error",
						code  : 3,
						reason: "User with such login/email already exists!"
				]

				PAResponse response = new PAResponse()

				response.response = JsonOutput.toJson(object)
				response.contentType = "application/json; charset=utf-8"

				return response // <-
			}

			user = manager.getById(id)

			user.login = login
			user.email = email
			user.password = password
			user.firstName = firstName
			user.lastName = lastName
			user.middleName = middleName

			if (user == null) {
				Object object = [
						status: "error",
						code  : 6,
						reason: "No such user!"
				]

				PAResponse response = new PAResponse()

				response.response = JsonOutput.toJson(object)
				response.contentType = "application/json; charset=utf-8"

				return response // <-

			}

			manager.alterUser(user)
		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}

		Object object = [
				status    : "success",
				"response": null
		]

		PAResponse response = new PAResponse()

		response.response = JsonOutput.toJson(object)
		response.contentType = "application/json; charset=utf-8"

		return response // <-

	}

	@Action
	private static PAResponse action$changeRank(Request request) {
		try {
			Integer id = Integer.valueOf((request.specialData as Map).get("user-id") as String)

			PARank newRank = PARank.valueOf((request.specialData as Map).get("rank") as String)
			PARank myRank = getRank(request.session)

			PAUser user = manager.getById(id)

			if (user == null) {
				Object object = [
						status: "error",
						code  : 6,
						reason: "No such user!"
				]

				PAResponse response = new PAResponse()

				response.response = JsonOutput.toJson(object)
				response.contentType = "application/json; charset=utf-8"

				return response // <-

			}

			if (!myRank.satisfies(C.CAN_EDIT_RANKS)) {
				Object object = [
						status: "error",
						code  : 4,
						reason: "You can't alter rank of this user!"
				]

				PAResponse response = new PAResponse()

				response.response = JsonOutput.toJson(object)
				response.contentType = "application/json; charset=utf-8"

				return response // <-
			}

			if (!myRank.higher(newRank)) {
				Object object = [
						status: "error",
						code  : 5,
						reason: "You can set rank only lower than you have!"
				]

				PAResponse response = new PAResponse()

				response.response = JsonOutput.toJson(object)
				response.contentType = "application/json; charset=utf-8"

				return response // <-
			}

			Object object = [
					status: "error",
					code  : 5,
					reason: "You can set rank only lower than you have!"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-

		} catch (Exception e) {
			e.printStackTrace()
			Object object = [
					status: "error",
					code  : -1,
					reason: "Unhandled system error! Reason: ${e.class.name} ${e.message}"
			]

			PAResponse response = new PAResponse()

			response.response = JsonOutput.toJson(object)
			response.contentType = "application/json; charset=utf-8"

			return response // <-
		}
	}
	// endregion

}
