package solru.okkeipatcher.domain.usecase.work

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.model.WorkState

interface GetWorkStateFlowUseCase {
	operator fun invoke(work: Work): Flow<WorkState>
}