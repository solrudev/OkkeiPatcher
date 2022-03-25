package solru.okkeipatcher.domain.exception

import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.model.LocalizedString

class LocalizedException(private val localizedMessage: LocalizedString, cause: Throwable? = null) : Exception(cause) {

	override val message: String
		get() = localizedMessage.resolve(OkkeiApplication.context).toString()
}