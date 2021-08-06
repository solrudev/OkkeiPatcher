package solru.okkeipatcher.exceptions

import solru.okkeipatcher.model.dto.Message

class OkkeiFatalException(messageData: Message, cause: Throwable?) :
	OkkeiException(messageData, cause)