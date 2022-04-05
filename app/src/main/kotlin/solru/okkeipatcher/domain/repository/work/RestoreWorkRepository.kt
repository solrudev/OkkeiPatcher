package solru.okkeipatcher.domain.repository.work

import solru.okkeipatcher.domain.model.Work

interface RestoreWorkRepository {
	suspend fun enqueueRestoreWork(): Work
	fun getRestoreWork(): Work?
}