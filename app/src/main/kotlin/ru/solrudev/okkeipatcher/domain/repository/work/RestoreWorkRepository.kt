package ru.solrudev.okkeipatcher.domain.repository.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface RestoreWorkRepository {
	suspend fun enqueueRestoreWork(): Work
	fun getRestoreWork(): Work?
}