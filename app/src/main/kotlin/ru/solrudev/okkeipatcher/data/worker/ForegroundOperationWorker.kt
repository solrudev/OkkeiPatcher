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

package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.data.service.NotificationService
import ru.solrudev.okkeipatcher.data.worker.model.WorkNotificationsParameters
import ru.solrudev.okkeipatcher.data.worker.util.setProgress
import ru.solrudev.okkeipatcher.data.worker.util.workerFailure
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.isEmpty
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.extension.statusAndAccumulatedProgress
import ru.solrudev.okkeipatcher.domain.operation.factory.OperationFactory
import kotlin.Throwable
import kotlin.Unit
import kotlin.time.Duration.Companion.milliseconds
import ru.solrudev.okkeipatcher.domain.core.Result as DomainResult

abstract class ForegroundOperationWorker(
	context: Context,
	workerParameters: WorkerParameters,
	private val workManager: WorkManager,
	private val operationFactory: OperationFactory<DomainResult<Unit>>,
	private val notificationService: NotificationService,
	private val workNotificationsParameters: WorkNotificationsParameters
) : CoroutineWorker(context, workerParameters) {

	final override suspend fun doWork(): Result {
		cancelOnRetry()
		try {
			setForeground(notificationService.createForegroundInfo())
			emitInitialStatus()
			val operation = operationFactory.create()
			operation.canInvoke().onFailure { failure ->
				return createFailure(failure.reason)
			}
			val result: DomainResult<Unit>
			coroutineScope {
				val observeOperationJob = operation.observeIn(this)
				result = operation()
				observeOperationJob.cancel()
			}
			result.onFailure { failure ->
				return createFailure(failure.reason)
			}
			return createSuccess()
		} catch (cancellationException: CancellationException) {
			throw cancellationException
		} catch (t: Throwable) {
			return createFailure(t)
		} finally {
			notificationService.close()
		}
	}

	private suspend fun cancelOnRetry() {
		if (runAttemptCount > 0) {
			notificationService.close()
			workManager.cancelWorkById(id).await()
		}
	}

	private fun createSuccess(): Result {
		notificationService.displayMessageNotification(workNotificationsParameters.successMessage)
		return Result.success()
	}

	private fun createFailure(reason: LocalizedString): Result {
		notificationService.displayMessageNotification(workNotificationsParameters.failureMessage)
		return workerFailure(reason)
	}

	private fun createFailure(exception: Throwable): Result {
		notificationService.displayMessageNotification(workNotificationsParameters.failureMessage)
		return workerFailure(exception)
	}

	private fun Operation<*>.observeIn(scope: CoroutineScope) = scope.launch {
		reportProgressIn(this)
		updateProgressNotificationIn(this)
		collectMessagesIn(this)
	}

	private fun Operation<*>.reportProgressIn(scope: CoroutineScope) = statusAndAccumulatedProgress()
		.onEach { (status, progress) ->
			setProgress(status, progress, progressMax)
		}
		.launchIn(scope)

	private fun Operation<*>.updateProgressNotificationIn(scope: CoroutineScope) = statusAndAccumulatedProgress()
		.onEach { (status, progress) ->
			val progressData = ProgressData(progress, progressMax)
			notificationService.updateProgressNotification(status, progressData)
			delay(500.milliseconds)
		}
		.launchIn(scope)

	private fun Operation<*>.collectMessagesIn(scope: CoroutineScope) = messages
		.onEach(notificationService::displayMessageNotification)
		.launchIn(scope)

	private suspend inline fun emitInitialStatus() {
		val status = workNotificationsParameters.initialStatus
		if (status.isEmpty()) {
			return
		}
		val progressData = ProgressData()
		setProgress(status, progressData.progress, progressData.max)
		notificationService.updateProgressNotification(status, progressData)
	}
}