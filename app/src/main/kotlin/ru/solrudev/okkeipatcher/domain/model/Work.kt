package ru.solrudev.okkeipatcher.domain.model

import androidx.annotation.Keep
import androidx.work.WorkInfo
import ru.solrudev.okkeipatcher.data.worker.ForegroundWorker
import ru.solrudev.okkeipatcher.domain.util.extension.getParcelable
import ru.solrudev.okkeipatcher.domain.util.extension.getSerializable
import java.io.Serializable
import java.util.*

/**
 * Represents long-running work.
 */
@Keep
data class Work(val id: UUID, val label: LocalizedString) : Serializable

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

fun WorkInfo.asWork(title: LocalizedString) = Work(id, title)

// TODO: make a mapper interface for WorkState instead of static extension
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