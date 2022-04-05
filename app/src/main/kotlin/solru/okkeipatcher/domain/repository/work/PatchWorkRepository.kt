package solru.okkeipatcher.domain.repository.work

import solru.okkeipatcher.domain.model.Work

interface PatchWorkRepository {
	suspend fun enqueuePatchWork(): Work
	fun getPatchWork(): Work?
}