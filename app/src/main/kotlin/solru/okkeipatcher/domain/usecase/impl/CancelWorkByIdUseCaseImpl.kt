package solru.okkeipatcher.domain.usecase.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.CancelWorkByIdUseCase
import java.util.*
import javax.inject.Inject

class CancelWorkByIdUseCaseImpl @Inject constructor() : CancelWorkByIdUseCase {

	override fun invoke(workId: UUID) {
		WorkManager.getInstance(OkkeiApplication.context).cancelWorkById(workId)
	}
}