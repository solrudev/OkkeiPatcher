package ru.solrudev.okkeipatcher.app.model

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed class Permission(
	val id: Int,
	val title: LocalizedString,
	val description: LocalizedString
) {

	object Storage : Permission(
		id = 0,
		title = LocalizedString.resource(R.string.permission_storage_title),
		description = LocalizedString.resource(R.string.permission_storage_description)
	)

	object Install : Permission(
		id = 1,
		title = LocalizedString.resource(R.string.permission_install_title),
		description = LocalizedString.resource(R.string.permission_install_description)
	)
}