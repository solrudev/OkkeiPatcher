package ru.solrudev.okkeipatcher.data.worker.model

import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message

data class WorkNotificationsParameters(
	val workLabel: LocalizedString,
	val successMessage: Message,
	val failureMessage: Message
)