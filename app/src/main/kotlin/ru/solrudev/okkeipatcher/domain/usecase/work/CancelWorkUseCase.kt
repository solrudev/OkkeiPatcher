package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface CancelWorkUseCase {
	operator fun invoke(work: Work)
}