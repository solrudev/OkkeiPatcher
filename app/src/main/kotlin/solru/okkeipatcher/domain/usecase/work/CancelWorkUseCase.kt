package solru.okkeipatcher.domain.usecase.work

import solru.okkeipatcher.domain.model.Work

interface CancelWorkUseCase {
	operator fun invoke(work: Work)
}