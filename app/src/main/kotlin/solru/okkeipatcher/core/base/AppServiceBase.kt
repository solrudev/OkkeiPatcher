package solru.okkeipatcher.core.base

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import solru.okkeipatcher.R
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.exceptions.io.HttpStatusCodeException
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.io.utils.extensions.download
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.Message
import solru.okkeipatcher.utils.extensions.empty
import solru.okkeipatcher.utils.extensions.reset
import java.io.File

abstract class AppServiceBase(protected val progressProvider: ProgressProviderImpl) :
	ProgressProvider by progressProvider, AppService {

	var isRunning = false
		protected set

	protected val statusMutable = MutableSharedFlow<LocalizedString>()

	protected val messageMutable = MutableSharedFlow<Message>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	override val status: Flow<LocalizedString> = statusMutable.asSharedFlow()
	override val message: Flow<Message> = messageMutable.asSharedFlow()

	protected suspend inline fun setStatusToAborted() {
		statusMutable.emit(LocalizedString.resource(R.string.status_aborted))
	}

	protected suspend fun finishTask() {
		progressProvider.mutableProgress.reset()
		isRunning = false
	}

	protected suspend inline fun sendWarningMessage(message: Int) {
		val warningMessage = Message(R.string.warning, message, R.string.dialog_ok)
		messageMutable.emit(warningMessage)
	}

	protected fun throwErrorMessage(
		message: Int,
		error: String = String.empty,
		cause: Throwable? = null
	): Nothing {
		throw OkkeiException(
			Message(R.string.error, message, R.string.dialog_ok, error = error),
			cause
		)
	}

	protected fun throwFatalErrorMessage(
		message: Int,
		error: String = String.empty,
		cause: Throwable? = null
	): Nothing {
		throw OkkeiException(
			Message(R.string.error, message, R.string.dialog_exit, error = error),
			cause
		)
	}

	protected fun Throwable.throwErrorMessage(
		message: Int,
		error: String = String.empty
	): Nothing = throwErrorMessage(message, error, this)

	protected fun Throwable.throwFatalErrorMessage(
		message: Int,
		error: String = String.empty
	): Nothing = throwFatalErrorMessage(message, error, this)

	protected suspend inline fun solru.okkeipatcher.io.base.File.downloadAndWrapException(url: String) =
		wrapDownloadException { downloadFrom(url) }

	protected suspend fun IoService.downloadAndWrapException(url: String, outputFile: File) =
		wrapDownloadException { download(url, outputFile, progressProvider.mutableProgress) }

	@Suppress("RedundantSuspendModifier")
	protected suspend inline fun wrapDownloadException(block: () -> Unit) {
		try {
			block()
		} catch (e: HttpStatusCodeException) {
			e.throwErrorMessage(
				R.string.error_http_file_access,
				error = "${e.statusCode.value} ${e.statusCode.description}"
			)
		} catch (e: Throwable) {
			e.throwErrorMessage(R.string.error_http_file_download)
		}
	}

	/**
	 * Guarantees setting status to aborted on exception and resetting progress before returning.
	 */
	protected suspend inline fun <T> tryWrapper(
		onCatch: (Throwable) -> Unit = {},
		onFinally: () -> Unit = {},
		block: () -> T
	): T {
		try {
			return block()
		} catch (e: Throwable) {
			withContext(NonCancellable) {
				setStatusToAborted()
			}
			onCatch(e)
			throw e
		} finally {
			withContext(NonCancellable) {
				finishTask()
			}
			onFinally()
		}
	}
}