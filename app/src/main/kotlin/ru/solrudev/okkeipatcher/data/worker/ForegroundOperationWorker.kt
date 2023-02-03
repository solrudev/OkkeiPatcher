package ru.solrudev.okkeipatcher.data.worker

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.worker.model.WorkNotificationsParameters
import ru.solrudev.okkeipatcher.data.worker.model.WorkerFailure
import ru.solrudev.okkeipatcher.data.worker.util.setProgress
import ru.solrudev.okkeipatcher.data.worker.util.workerFailure
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.extension.statusAndAccumulatedProgress
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.domain.operation.factory.OperationFactory
import kotlin.Throwable
import kotlin.getValue
import kotlin.lazy
import kotlin.time.Duration.Companion.milliseconds
import ru.solrudev.okkeipatcher.domain.core.Result as DomainResult

abstract class ForegroundOperationWorker(
	context: Context,
	workerParameters: WorkerParameters,
	private val notificationServiceFactory: NotificationServiceFactory,
	private val workManager: WorkManager,
	private val operationFactory: OperationFactory<DomainResult>,
	private val workNotificationsParameters: WorkNotificationsParameters
) : CoroutineWorker(context, workerParameters) {

	private val notificationService by lazy {
		notificationServiceFactory.create(workNotificationsParameters.workLabel, createNotificationsContentIntent())
	}

	final override suspend fun doWork(): Result {
		cancelIfRetry()
		try {
			setForeground(notificationService.createForegroundInfo())
			val operation = operationFactory.create()
			operation.canInvoke().onFailure { failure ->
				return createFailure(WorkerFailure.Domain(failure.reason))
			}
			val result: DomainResult
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
		} finally {
			withContext(NonCancellable) {
				notificationService.clearShownMessageNotifications()
				delay(250.milliseconds) // for foreground service notification to be canceled before returning
			}
		}
	}

	protected open fun createNotificationsContentIntent(): PendingIntent {
		val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
		val flagImmutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
		return PendingIntent.getActivity(
			applicationContext, 0, launchIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or flagImmutable
		)
	}

	private suspend fun cancelIfRetry() {
		if (runAttemptCount > 0) {
			workManager.cancelWorkById(id).await()
		}
	}

	private suspend fun createSuccess(): Result {
		withContext(NonCancellable) {
			notificationService.displayResultNotification(workNotificationsParameters.successMessage)
		}
		return Result.success()
	}

	private suspend fun createFailure(failure: WorkerFailure): Result {
		withContext(NonCancellable) {
			notificationService.displayResultNotification(workNotificationsParameters.failureMessage)
		}
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