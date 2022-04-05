package ru.solrudev.okkeipatcher.domain.usecase.work

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.WorkState

interface GetWorkStateFlowUseCase {
	operator fun invoke(work: Work): Flow<WorkState>
}