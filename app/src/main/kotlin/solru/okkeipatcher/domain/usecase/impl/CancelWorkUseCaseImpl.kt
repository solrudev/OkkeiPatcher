package solru.okkeipatcher.domain.usecase.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.CancelWorkUseCase
import java.util.*
import javax.inject.Inject

class CancelWorkUseCaseImpl @Inject constructor() : CancelWorkUseCase {

	override fun invoke(workId: UUID) {
		WorkManager.getInstance(OkkeiApplication.context).cancelWorkById(workId)
	}
}