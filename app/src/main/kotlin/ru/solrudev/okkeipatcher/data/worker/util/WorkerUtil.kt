/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.worker.util

import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.app.model.WorkState
import ru.solrudev.okkeipatcher.data.worker.model.WorkerFailure
import ru.solrudev.okkeipatcher.data.worker.util.extension.getSerializable
import ru.solrudev.okkeipatcher.data.worker.util.extension.putSerializable
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

private const val PROGRESS = "progress"
private const val PROGRESS_MAX = "progress_max"
private const val STATUS = "status"
private const val FAILURE_REASON = "failure_reason"
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

fun workerFailure(data: WorkerFailure) = ListenableWorker.Result.failure(
	Data.Builder().apply {
		when (data) {
			is WorkerFailure.Domain -> putSerializable(FAILURE_REASON, data.reason)
			is WorkerFailure.Unhandled -> putString(
				STACK_TRACE,
				data.exception
					.stackTraceToString()
					.take(5000)
			)
		}
	}.build()
)

fun WorkInfo?.toWorkState() = when (this?.state) {
	WorkInfo.State.RUNNING -> with(progress) {
		val status = getSerializable<LocalizedString>(STATUS) ?: LocalizedString.empty()
		val progress = getInt(PROGRESS, 0)
		val progressMax = getInt(PROGRESS_MAX, 0)
		val progressData = ProgressData(progress, progressMax)
		WorkState.Running(status, progressData)
	}
	WorkInfo.State.FAILED -> with(outputData) {
		val reason = getSerializable(FAILURE_REASON) ?: LocalizedString.empty()
		val stackTrace = getString(STACK_TRACE) ?: ""
		WorkState.Failed(reason, stackTrace)
	}
	WorkInfo.State.SUCCEEDED -> WorkState.Succeeded
	WorkInfo.State.CANCELLED -> WorkState.Canceled
	else -> WorkState.Unknown
}