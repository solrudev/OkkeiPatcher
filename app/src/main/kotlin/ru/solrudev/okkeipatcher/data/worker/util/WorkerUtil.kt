package ru.solrudev.okkeipatcher.data.worker.util

import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkInfo
import ru.solrudev.okkeipatcher.data.repository.work.mapper.WorkStateMapper
import ru.solrudev.okkeipatcher.data.worker.util.extension.getSerializable
import ru.solrudev.okkeipatcher.data.worker.util.extension.putSerializable
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.domain.model.WorkState
import javax.inject.Inject

private const val PROGRESS = "progress"
private const val PROGRESS_MAX = "progress_max"
private const val STATUS = "status"
private const val STACK_TRACE = "stack_trace"

suspend fun CoroutineWorker.setProgress(
	status: LocalizedString,
	progress: Int,
	progressMax: Int
) = setProgress(
	Data.Builder()
		.putSerializable(STATUS, status)
		.putInt(PROGRESS, progress)
		.putInt(PROGRESS_MAX, progressMax)
		.build()
)

// TODO
fun failureWorkData(exception: Throwable) = Data.Builder()
	.putString(
		STACK_TRACE,
		exception
			.stackTraceToString()
			.take(5000)
	)
	.build()

class WorkStateMapperImpl @Inject constructor() : WorkStateMapper {

	override fun invoke(workInfo: WorkInfo?) = when (workInfo?.state) {
		WorkInfo.State.RUNNING -> with(workInfo.progress) {
			val status = getSerializable<LocalizedString>(STATUS) ?: LocalizedString.empty()
			val progress = getInt(PROGRESS, 0)
			val progressMax = getInt(PROGRESS_MAX, 0)
			val progressData = ProgressData(progress, progressMax)
			WorkState.Running(status, progressData)
		}
		WorkInfo.State.FAILED -> {
			val stackTrace = workInfo.outputData.getString(STACK_TRACE) ?: ""
			WorkState.Failed(stackTrace)
		}
		WorkInfo.State.SUCCEEDED -> WorkState.Succeeded
		WorkInfo.State.CANCELLED -> WorkState.Canceled
		else -> WorkState.Unknown
	}
}