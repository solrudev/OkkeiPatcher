package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface GetIsWorkPendingUseCase {
	suspend operator fun invoke(work: Work): Boolean
}