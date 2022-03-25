package solru.okkeipatcher.domain.model

import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.util.extension.getParcelable
import solru.okkeipatcher.domain.util.extension.getSerializable
import solru.okkeipatcher.domain.worker.ForegroundWorker
import java.util.*

/**
 * Represents long-running work.
 */
data class Work(
	val id: UUID
) {

	/**
	 * A cold [Flow] of [WorkState] for this work.
	 */
	val state: Flow<WorkState>
		get() = flow {
			supervisorScope {
				WorkManager.getInstance(OkkeiApplication.context)
					.getWorkInfoByIdLiveData(id)
					.asFlow()
					.collect { workInfo ->
						val workState = workInfo.asWorkState()
						emit(workState)
						if (workState.isFinished) {
							this@supervisorScope.cancel()
						}
					}
			}
		}

	/**
	 * Cancels this work.
	 */
	fun cancel() {
		WorkManager.getInstance(OkkeiApplication.context).cancelWorkById(id)
	}
}

/**
 * Represents a [Work] state.
 */
sealed class WorkState {

	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkState()
	data class Failed(val throwable: Throwable?) : WorkState()
	object Succeeded : WorkState()
	object Canceled : WorkState()
	object Unknown : WorkState()

	/**
	 * Returns true for [Failed], [Succeeded] and [Canceled] states.
	 */
	val isFinished: Boolean
		get() = this is Failed || this is Succeeded || this is Canceled
}

fun WorkInfo.asWork() = Work(id)

fun WorkInfo?.asWorkState() = when (this?.state) {
	WorkInfo.State.RUNNING -> with(progress) {
		val status = getSerializable<LocalizedString>(ForegroundWorker.KEY_STATUS) ?: LocalizedString.empty()
		val progressData = getParcelable(ForegroundWorker.KEY_PROGRESS_DATA) ?: ProgressData()
		WorkState.Running(status, progressData)
	}
	WorkInfo.State.FAILED -> {
		val throwable = outputData.getSerializable<Throwable>(ForegroundWorker.KEY_FAILURE_CAUSE)
		WorkState.Failed(throwable)
	}
	WorkInfo.State.SUCCEEDED -> WorkState.Succeeded
	WorkInfo.State.CANCELLED -> WorkState.Canceled
	else -> WorkState.Unknown
}