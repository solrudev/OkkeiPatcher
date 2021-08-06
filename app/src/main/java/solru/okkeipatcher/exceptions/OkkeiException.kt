package solru.okkeipatcher.exceptions

import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.model.dto.Message

open class OkkeiException(val messageData: Message, override val cause: Throwable? = null) :
	Exception(MainApplication.context.getString(messageData.messageId))