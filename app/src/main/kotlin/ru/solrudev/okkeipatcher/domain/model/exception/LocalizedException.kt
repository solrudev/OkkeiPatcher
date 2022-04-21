package ru.solrudev.okkeipatcher.domain.model.exception

import ru.solrudev.okkeipatcher.OkkeiApplication
import ru.solrudev.okkeipatcher.domain.model.LocalizedString

class LocalizedException(private val localizedMessage: LocalizedString, cause: Throwable? = null) : Exception(cause) {

	override val message: String
		get() = localizedMessage.resolve(OkkeiApplication.context).toString()
}