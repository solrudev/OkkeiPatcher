package solru.okkeipatcher.domain.usecase

import java.util.*

interface CancelWorkUseCase {
	operator fun invoke(workId: UUID)
}