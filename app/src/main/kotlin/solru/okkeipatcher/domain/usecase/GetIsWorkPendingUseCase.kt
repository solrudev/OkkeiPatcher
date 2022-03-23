package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface GetIsWorkPendingUseCase {
	suspend operator fun invoke(work: Work): Boolean
}