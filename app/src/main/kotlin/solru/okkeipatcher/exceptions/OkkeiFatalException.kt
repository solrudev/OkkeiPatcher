package solru.okkeipatcher.exceptions

import solru.okkeipatcher.model.LocalizedString

class OkkeiFatalException(localizedMessage: LocalizedString, cause: Throwable?) :
	OkkeiException(localizedMessage, cause)