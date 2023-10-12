/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.app.model

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed class Permission(
	val id: Int,
	val title: LocalizedString,
	val description: LocalizedString
) {

	data object Storage : Permission(
		id = 0,
		title = LocalizedString.resource(R.string.permission_storage_title),
		description = LocalizedString.resource(R.string.permission_storage_description)
	)

	data object Install : Permission(
		id = 1,
		title = LocalizedString.resource(R.string.permission_install_title),
		description = LocalizedString.resource(R.string.permission_install_description)
	)
}