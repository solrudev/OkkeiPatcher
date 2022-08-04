package ru.solrudev.okkeipatcher.domain.repository.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface ConcreteWorkRepository {
	suspend fun enqueueWork(): Work
	fun getWork(): Work?
}

interface PatchWorkRepository : ConcreteWorkRepository
interface RestoreWorkRepository : ConcreteWorkRepository