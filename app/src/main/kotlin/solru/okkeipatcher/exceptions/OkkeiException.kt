package solru.okkeipatcher.exceptions

import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.model.dto.Message

open class OkkeiException(val messageData: Message, override val cause: Throwable? = null) :
	Exception(OkkeiApplication.context.getString(messageData.messageId))