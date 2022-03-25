package solru.okkeipatcher.domain.usecase.work

import solru.okkeipatcher.domain.model.Work

interface GetIsWorkPendingUseCase {
	suspend operator fun invoke(work: Work): Boolean
}