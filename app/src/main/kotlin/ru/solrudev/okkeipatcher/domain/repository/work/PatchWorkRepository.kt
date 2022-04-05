package ru.solrudev.okkeipatcher.domain.repository.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface PatchWorkRepository {
	suspend fun enqueuePatchWork(): Work
	fun getPatchWork(): Work?
}