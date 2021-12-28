package solru.okkeipatcher.exceptions

import solru.okkeipatcher.data.LocalizedString

class OkkeiFatalException(localizedMessage: LocalizedString, cause: Throwable?) :
	OkkeiException(localizedMessage, cause)