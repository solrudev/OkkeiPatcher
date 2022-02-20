package solru.okkeipatcher.domain.usecase.impl

import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.ProgressData
import solru.okkeipatcher.data.WorkState
import solru.okkeipatcher.domain.usecase.GetWorkStateFlowByIdUseCase
import solru.okkeipatcher.domain.workers.ForegroundWorker
import solru.okkeipatcher.utils.extensions.getSerializable
import java.util.*
import javax.inject.Inject

class GetWorkStateFlowByIdUseCaseImpl @Inject constructor() : GetWorkStateFlowByIdUseCase {

	override fun invoke(workId: UUID) = flow {
		supervisorScope {
			WorkManager.getInstance(OkkeiApplication.context)
				.getWorkInfoByIdLiveData(workId)
				.asFlow()
				.collect { workInfo ->
					if (workInfo == null) {
						emit(WorkState.Unknown)
					}
					with(workInfo.progress) {
						val status: LocalizedString =
							getSerializable(ForegroundWorker.KEY_STATUS) ?: LocalizedString.empty()
						val progressData = getSerializable(ForegroundWorker.KEY_PROGRESS_DATA) ?: ProgressData()
						emit(WorkState.Running(status, progressData))
					}
					when (workInfo.state) {
						WorkInfo.State.SUCCEEDED -> emit(WorkState.Succeeded)
						WorkInfo.State.CANCELLED -> emit(WorkState.Canceled)
						WorkInfo.State.FAILED -> {
							val throwable =
								workInfo.outputData.getSerializable<Throwable>(ForegroundWorker.KEY_FAILURE_CAUSE)
							emit(WorkState.Failed(throwable))
						}
						else -> {}
					}
					if (workInfo.state.isFinished) {
						WorkManager.getInstance(OkkeiApplication.context).pruneWork()
						this@supervisorScope.cancel()
					}
				}
		}
	}
}