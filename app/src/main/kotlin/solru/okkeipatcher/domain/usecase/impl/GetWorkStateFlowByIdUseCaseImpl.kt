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
import solru.okkeipatcher.utils.extensions.getParcelable
import solru.okkeipatcher.utils.extensions.getSerializable
import solru.okkeipatcher.workers.ForegroundWorker
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
						return@collect
					}
					when (workInfo.state) {
						WorkInfo.State.RUNNING -> with(workInfo.progress) {
							val status =
								getSerializable<LocalizedString>(ForegroundWorker.KEY_STATUS) ?: LocalizedString.empty()
							val progressData = getParcelable(ForegroundWorker.KEY_PROGRESS_DATA) ?: ProgressData()
							emit(WorkState.Running(status, progressData))
						}
						WorkInfo.State.FAILED -> {
							val throwable =
								workInfo.outputData.getSerializable<Throwable>(ForegroundWorker.KEY_FAILURE_CAUSE)
							emit(WorkState.Failed(throwable))
						}
						WorkInfo.State.SUCCEEDED -> emit(WorkState.Succeeded)
						WorkInfo.State.CANCELLED -> emit(WorkState.Canceled)
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