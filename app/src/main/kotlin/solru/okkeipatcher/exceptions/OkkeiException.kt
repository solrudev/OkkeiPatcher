package solru.okkeipatcher.exceptions

import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.LocalizedString

open class OkkeiException(val localizedMessage: LocalizedString, cause: Throwable? = null) : Exception(cause) {

	override val message: String
		get() = localizedMessage.resolve(OkkeiApplication.context).toString()
}