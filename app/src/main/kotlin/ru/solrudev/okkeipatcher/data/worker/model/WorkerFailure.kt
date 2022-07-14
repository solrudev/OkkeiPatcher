package ru.solrudev.okkeipatcher.data.worker.model

import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed interface WorkerFailure {
	data class Domain(val reason: LocalizedString) : WorkerFailure
	data class Unhandled(val exception: Throwable) : WorkerFailure
}