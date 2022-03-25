package solru.okkeipatcher.domain.repository

import solru.okkeipatcher.domain.model.Work

interface WorkRepository {
	suspend fun add(work: Work)
	suspend fun updateIsPending(work: Work, isPending: Boolean)
	suspend fun getIsPending(work: Work): Boolean
}