package solru.okkeipatcher.pm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.ProgressProvider
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.pm.activityresult.PreLollipopInstallPackageContract
import solru.okkeipatcher.pm.utils.clearTurnScreenOnSettings
import solru.okkeipatcher.pm.utils.showNotification
import solru.okkeipatcher.pm.utils.turnScreenOnWhenLocked
import solru.okkeipatcher.utils.extensions.makeIndeterminate
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.extensions.tryEmit
import solru.okkeipatcher.utils.extensions.tryReset
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val REGISTRY_KEY = "OkkeiPatcher_PackageInstaller"
private const val ACTION_INSTALLATION_STATUS_NOTIFICATION =
	"okkeipatcher.INSTALLATION_STATUS_NOTIFICATION"
private const val REQUEST_CODE = 6541

object PackageInstaller : ProgressProvider {

	@EntryPoint
	@InstallIn(SingletonComponent::class)
	interface PackageInstallerEntryPoint {
		fun getIoDispatcher(): CoroutineDispatcher
		fun getIoService(): IoService
	}

	var isInstalling = false
		private set

	private val progressMutable = MutableSharedFlow<ProgressData>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	override val progress: Flow<ProgressData> = progressMutable.asSharedFlow()

	@JvmStatic
	private var NOTIFICATION_ID = 18475

	private val ioDispatcher = EntryPointAccessors.fromApplication(
		MainApplication.context,
		PackageInstallerEntryPoint::class.java
	).getIoDispatcher()

	private val ioService = EntryPointAccessors.fromApplication(
		MainApplication.context,
		PackageInstallerEntryPoint::class.java
	).getIoService()

	private lateinit var capturedContinuation: CancellableContinuation<Boolean>
	private val contract = PreLollipopInstallPackageContract()
	private var shouldContinue = true

	private val installFinishedCallback = MutableSharedFlow<Unit>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	private val installationEventsReceiver = object : BroadcastReceiver() {
		@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
		override fun onReceive(context: Context, intent: Intent) {
			if (intent.action != ACTION_INSTALLATION_STATUS_NOTIFICATION) {
				return
			}
			when (intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)) {
				PackageInstaller.STATUS_PENDING_USER_ACTION -> {
					val confirmationIntent =
						intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
							?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					context.startActivity(confirmationIntent)
				}
				PackageInstaller.STATUS_SUCCESS -> {
					LocalBroadcastManager.getInstance(MainApplication.context)
						.unregisterReceiver(this)
					finishInstallation(true)
				}
				else -> {
					LocalBroadcastManager.getInstance(MainApplication.context)
						.unregisterReceiver(this)
					finishInstallation(false)
				}
			}
		}
	}

	suspend fun installPackage(apkFile: File): Boolean {
		val capturedCoroutineContext = coroutineContext
		return suspendCancellableCoroutine { continuation ->
			var session: PackageInstaller.Session? = null
			continuation.invokeOnCancellation {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) abandonSession(session)
				onCancellation()
			}
			capturedContinuation = continuation
			if (isInstalling) {
				continuation.resumeWithException(
					IllegalStateException("Can't install while another install session is active")
				)
			}
			isInstalling = true
			CoroutineScope(capturedCoroutineContext + ioDispatcher).launch {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					progressMutable.makeIndeterminate()
					displayNotification(Uri.fromFile(apkFile))
				} else {
					LocalBroadcastManager.getInstance(MainApplication.context).registerReceiver(
						installationEventsReceiver, IntentFilter(
							ACTION_INSTALLATION_STATUS_NOTIFICATION
						)
					)
					val packageInstaller = MainApplication.context.packageManager.packageInstaller
					val sessionParams =
						PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						sessionParams.setInstallReason(PackageManager.INSTALL_REASON_USER)
					}
					val sessionId = packageInstaller.createSession(sessionParams)
					session = packageInstaller.openSession(sessionId)
					val observer = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
					object : PackageInstaller.SessionCallback() {
						override fun onCreated(sessionId: Int) {}
						override fun onBadgingChanged(sessionId: Int) {}
						override fun onActiveChanged(sessionId: Int, active: Boolean) {}

						override fun onProgressChanged(sessionId: Int, progress: Float) {
							progressMutable.tryEmit((progress * 100).toInt(), 100)
						}

						override fun onFinished(sessionId: Int, success: Boolean) {
							packageInstaller.unregisterSessionCallback(this)
						}
					}
					try {
						withContext(Dispatchers.Main) {
							packageInstaller.registerSessionCallback(observer)
						}
						copyApkToSession(apkFile, session!!)
						val intent = Intent(
							MainApplication.context,
							InstallationEventsReceiver::class.java
						).apply {
							action = ACTION_INSTALLATION_STATUS_NOTIFICATION
						}
						val pendingIntent = PendingIntent.getBroadcast(
							MainApplication.context,
							REQUEST_CODE,
							intent,
							PendingIntent.FLAG_UPDATE_CURRENT
						)
						val statusReceiver = pendingIntent.intentSender
						displayNotification()
						waitForUserAction()
						session!!.commit(statusReceiver)
					} catch (e: Throwable) {
						abandonSession(session)
						observer.onFinished(sessionId, false)
						LocalBroadcastManager.getInstance(MainApplication.context)
							.unregisterReceiver(installationEventsReceiver)
						withContext(NonCancellable) {
							progressMutable.reset()
							installFinishedCallback.emit(Unit)
						}
						isInstalling = false
						continuation.resumeWithException(e)
					} finally {
						session?.close()
					}
				}
			}
		}
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
	private suspend inline fun copyApkToSession(apkFile: File, session: PackageInstaller.Session) =
		coroutineScope {
			val apkStream = FileInputStream(apkFile)
			val sessionStream =
				session.openWrite("okkei_temp.apk", 0, apkFile.length())
			val progressFlow = MutableSharedFlow<ProgressData>(
				replay = 0,
				extraBufferCapacity = 1,
				onBufferOverflow = BufferOverflow.DROP_OLDEST
			)
			val progressJob = launch {
				progressFlow.collect {
					session.setStagingProgress(it.progress.toFloat() / it.max)
				}
			}
			ioService.copy(apkStream, sessionStream, apkFile.length(), progressFlow)
			progressJob.cancel()
		}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
	private fun abandonSession(session: PackageInstaller.Session?) {
		try {
			session?.abandon()
		} catch (_: Throwable) {
		}
	}

	private fun onCancellation() {
		shouldContinue = true
		isInstalling = false
		val notificationManager =
			MainApplication.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NOTIFICATION_ID)
	}

	private fun finishInstallation(result: Boolean) {
		progressMutable.tryReset()
		onCancellation()
		installFinishedCallback.tryEmit(Unit)
		capturedContinuation.resume(result)
	}

	private fun displayNotification(apkUri: Uri? = null) {
		val activityIntent =
			Intent(MainApplication.context, InstallResultReceiverActivity::class.java).apply {
				if (apkUri != null) putExtra(REGISTRY_KEY, apkUri)
			}
		val fullScreenIntent = PendingIntent.getActivity(
			MainApplication.context,
			REQUEST_CODE,
			activityIntent,
			PendingIntent.FLAG_CANCEL_CURRENT
		)
		showNotification(
			fullScreenIntent,
			++NOTIFICATION_ID,
			R.string.prompt_install_title,
			R.string.prompt_install
		)
	}

	private var sessionCommitContinuation: CancellableContinuation<Unit>? = null

	private suspend inline fun waitForUserAction() = suspendCancellableCoroutine<Unit> {
		it.invokeOnCancellation { onCancellation() }
		sessionCommitContinuation = it
	}

	class InstallResultReceiverActivity : AppCompatActivity() {

		private var onRestartCalled = false

		private val installLauncher = registerForActivityResult(contract) {
			finishInstallation(it)
		}

		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			turnScreenOnWhenLocked()
			if (shouldContinue && !isRestarted(savedInstanceState)) {
				shouldContinue = false
				sessionCommitContinuation?.resume(Unit)
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				onRestartCalled = false
				finish()
				return
			}
			lifecycleScope.launchWhenResumed {
				installFinishedCallback.collect { finish() }
			}
			if (isRestarted(savedInstanceState)) {
				onRestartCalled = false
				return
			}
			val apkUri = intent.extras?.getParcelable<Uri>(REGISTRY_KEY)
			installLauncher.launch(apkUri)
		}

		override fun onRestart() {
			super.onRestart()
			onRestartCalled = true
		}

		override fun onSaveInstanceState(outState: Bundle) {
			super.onSaveInstanceState(outState)
			outState.putBoolean(KEY_RECREATED, true)
		}

		override fun onDestroy() {
			super.onDestroy()
			shouldContinue = false
			clearTurnScreenOnSettings()
		}

		private fun isRestarted(savedInstanceState: Bundle?) =
			savedInstanceState?.getBoolean(KEY_RECREATED) == true || onRestartCalled

		companion object {
			private const val KEY_RECREATED = "RECREATED"
		}
	}
}