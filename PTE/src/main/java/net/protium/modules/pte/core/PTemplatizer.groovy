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

interface PTemplatizer {

	PTemplatizer register(String name, Object data)

	PTemplatizer register(Map<String, ?> list)

	PTemplate get(String name)

}