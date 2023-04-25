/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
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

import android.app.PendingIntent
import android.content.Context
import android.os.Build
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
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.worker.model.WorkNotificationsParameters
import ru.solrudev.okkeipatcher.data.worker.model.WorkerFailure
import ru.solrudev.okkeipatcher.data.worker.util.setProgress
import ru.solrudev.okkeipatcher.data.worker.util.workerFailure
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.extension.statusAndAccumulatedProgress
import ru.solrudev.okkeipatcher.domain.operation.factory.OperationFactory
import kotlin.Throwable
import kotlin.Unit
import kotlin.getValue
import kotlin.lazy
import kotlin.time.Duration.Companion.milliseconds
import ru.solrudev.okkeipatcher.domain.core.Result as DomainResult

abstract class ForegroundOperationWorker(
	context: Context,
	workerParameters: WorkerParameters,
	private val notificationServiceFactory: NotificationServiceFactory,
	private val workManager: WorkManager,
	private val operationFactory: OperationFactory<DomainResult<Unit>>,
	private val workNotificationsParameters: WorkNotificationsParameters
) : CoroutineWorker(context, workerParameters) {

	private val notificationService by lazy {
		notificationServiceFactory.create(workNotificationsParameters.workLabel, createNotificationsContentIntent())
	}

	final override suspend fun doWork(): Result {
		cancelOnRetry()
		try {
			setForeground(notificationService.createForegroundInfo())
			val operation = operationFactory.create()
			operation.canInvoke().onFailure { failure ->
				return createFailure(WorkerFailure.Domain(failure.reason))
			}
			val result: DomainResult<Unit>
			coroutineScope {
				val observeOperationJob = operation.observeIn(this)
				result = operation()
				observeOperationJob.cancel()
			}
			result.onFailure { failure ->
				return createFailure(WorkerFailure.Domain(failure.reason))
			}
			return createSuccess()
		} catch (cancellationException: CancellationException) {
			throw cancellationException
		} catch (t: Throwable) {
			return createFailure(WorkerFailure.Unhandled(t))
		}
	}

	protected open fun createNotificationsContentIntent() = defaultNotificationIntent()

	private suspend fun cancelOnRetry() {
		if (runAttemptCount > 0) {
			workManager.cancelWorkById(id).await()
		}
	}

	private fun createSuccess(): Result {
		notificationService.displayMessageNotification(workNotificationsParameters.successMessage)
		return Result.success()
	}

	private fun createFailure(failure: WorkerFailure): Result {
		notificationService.displayMessageNotification(workNotificationsParameters.failureMessage)
		return workerFailure(failure)
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
}

fun ForegroundOperationWorker.defaultNotificationIntent(): PendingIntent {
	val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
	val flagImmutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
	return PendingIntent.getActivity(
		applicationContext, 0, launchIntent,
		PendingIntent.FLAG_UPDATE_CURRENT or flagImmutable
	)
}