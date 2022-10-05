package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.service.NotificationService
import ru.solrudev.okkeipatcher.data.worker.model.WorkerFailure
import ru.solrudev.okkeipatcher.data.worker.util.setProgress
import ru.solrudev.okkeipatcher.data.worker.util.workerFailure
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.extension.statusAndAccumulatedProgress
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.domain.service.operation.factory.OperationFactory
import kotlin.Throwable
import kotlin.time.Duration.Companion.milliseconds
import ru.solrudev.okkeipatcher.domain.core.Result as DomainResult

abstract class ForegroundWorker(
	context: Context,
	workerParameters: WorkerParameters,
	private val notificationService: NotificationService,
	private val workManager: WorkManager,
	private val operationFactory: OperationFactory<DomainResult>,
	workLabel: LocalizedString
) : CoroutineWorker(context, workerParameters) {

	init {
		notificationService.progressNotificationTitle = workLabel
	}

	final override suspend fun doWork(): Result {
		cancelIfRetry()
		try {
			setForeground(createForegroundInfo())
			val operation = operationFactory.create()
			operation.canInvoke().onFailure { failure ->
				return createFailure(WorkerFailure.Domain(failure.reason))
			}
			val result: DomainResult
			coroutineScope {
				val observeJob = observeOperation(operation)
				result = operation()
				observeJob.cancel()
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

	private suspend fun cancelIfRetry() {
		if (runAttemptCount > 0) {
			workManager
				.cancelWorkById(id)
				.await()
		}
	}

	private fun createForegroundInfo() = ForegroundInfo(
		notificationService.progressNotificationId,
		notificationService.getProgressNotification()
	)

	private suspend fun createSuccess(): Result {
		val successMessage = Message(
			LocalizedString.resource(R.string.notification_title_work_finished),
			LocalizedString.resource(R.string.notification_message_work_success)
		)
		withContext(NonCancellable) { notificationService.displayResultNotification(successMessage) }
		return Result.success()
	}

	private suspend fun createFailure(failure: WorkerFailure): Result {
		val failMessage = Message(
			LocalizedString.resource(R.string.notification_title_work_finished),
			LocalizedString.resource(R.string.notification_message_work_failed)
		)
		withContext(NonCancellable) { notificationService.displayResultNotification(failMessage) }
		return workerFailure(failure)
	}

	private fun CoroutineScope.observeOperation(operation: Operation<*>) = launch {
		reportProgress(operation)
		updateProgressNotification(operation)
		collectMessages(operation)
	}

	private fun CoroutineScope.reportProgress(operation: Operation<*>) = operation
		.statusAndAccumulatedProgress()
		.onEach { (status, progress) ->
			setProgress(status, progress, operation.progressMax)
		}
		.launchIn(this)

	private fun CoroutineScope.updateProgressNotification(operation: Operation<*>) = operation
		.statusAndAccumulatedProgress()
		.onEach { (status, progress) ->
			val progressData = ProgressData(progress, operation.progressMax)
			notificationService.updateProgressNotification(status, progressData)
			delay(500.milliseconds)
		}
		.launchIn(this)

	private fun CoroutineScope.collectMessages(operation: Operation<*>) = operation.messages
		.onEach(notificationService::displayMessageNotification)
		.launchIn(this)
}