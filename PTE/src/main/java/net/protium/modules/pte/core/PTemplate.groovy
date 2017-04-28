/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.modules.pte.core

/*
In net.protium.modules.pte
From temporary-protium
*/

interface PTemplate {

	PTemplate bind(String name, data)

	PTemplate bind(Map<String, ?> data)

	PTemplate execute()

	@Override
	String toString()
}
