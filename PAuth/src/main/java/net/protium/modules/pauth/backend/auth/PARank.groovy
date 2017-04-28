/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pauth.backend.auth

/*
In net.protium.modules.pauth.database.orm.user
From temporary-protium
*/

enum PARank {
	GST(0), // guest
	USR(1), // regular user
	PWU(2), // power user
	SPC(4), // specialist
	MOD(16), // moderator
	ADM(256), // administrator
	GOD(65536) // site maintainer

	private Integer grade = 0

	private PARank(Integer grade) {
		this.grade = grade
	}

	boolean satisfies(PARank needed) {
		this.grade >= needed.grade
	}

	boolean higher(PARank needed) {
		this.grade > needed.grade
	}
}
