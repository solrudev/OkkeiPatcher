package solru.okkeipatcher.domain.usecase

import java.util.*

interface CancelWorkByIdUseCase {
	operator fun invoke(workId: UUID)
}