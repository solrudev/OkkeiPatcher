package solru.okkeipatcher.exceptions

import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.model.LocalizedString

open class OkkeiException(val localizedMessage: LocalizedString, cause: Throwable? = null) :
	Exception(localizedMessage.resolve(OkkeiApplication.context).toString(), cause)