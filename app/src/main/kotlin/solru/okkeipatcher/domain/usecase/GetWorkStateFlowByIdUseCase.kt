package solru.okkeipatcher.domain.usecase

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.data.WorkState
import java.util.*

interface GetWorkStateFlowByIdUseCase {
	operator fun invoke(workId: UUID): Flow<WorkState>
}