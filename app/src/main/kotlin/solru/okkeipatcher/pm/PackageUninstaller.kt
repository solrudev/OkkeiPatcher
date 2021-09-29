package solru.okkeipatcher.pm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.pm.activityresult.UninstallPackageContract
import solru.okkeipatcher.pm.utils.clearTurnScreenOnSettings
import solru.okkeipatcher.pm.utils.showNotification
import solru.okkeipatcher.pm.utils.turnScreenOnWhenLocked
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val PACKAGE_NAME_KEY = "PACKAGE_UNINSTALLER_PACKAGE_NAME"
private const val REQUEST_CODE = 4127

object PackageUninstaller {

	var hasActiveSession = false
		private set

	@JvmStatic
	private var NOTIFICATION_ID = 34187

	private lateinit var capturedContinuation: Continuation<Boolean>
	private val contract = UninstallPackageContract()

	suspend fun uninstallPackage(packageName: String) =
		suspendCancellableCoroutine<Boolean> { continuation ->
			continuation.invokeOnCancellation { onCancellation() }
			if (hasActiveSession) {
				continuation.resumeWithException(
					IllegalStateException("Can't uninstall while another uninstall session is active")
				)
			}
			hasActiveSession = true
			capturedContinuation = continuation
			val activityIntent =
				Intent(MainApplication.context, UninstallResultReceiverActivity::class.java).apply {
					putExtra(PACKAGE_NAME_KEY, packageName)
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
				R.string.prompt_uninstall_title,
				R.string.prompt_uninstall
			)
		}

	private fun onCancellation() {
		hasActiveSession = false
		val notificationManager =
			MainApplication.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NOTIFICATION_ID)
	}

	class UninstallResultReceiverActivity : AppCompatActivity() {

		private var onRestartCalled = false

		private val uninstallLauncher = registerForActivityResult(contract) {
			onCancellation()
			capturedContinuation.resume(it)
			finish()
		}

		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			turnScreenOnWhenLocked()
			if (isRestarted(savedInstanceState)) {
				onRestartCalled = false
				return
			}
			val packageName = intent.extras?.getString(PACKAGE_NAME_KEY)
			uninstallLauncher.launch(packageName)
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
			clearTurnScreenOnSettings()
		}

		private fun isRestarted(savedInstanceState: Bundle?) =
			savedInstanceState?.getBoolean(KEY_RECREATED) == true || onRestartCalled

		companion object {
			private const val KEY_RECREATED = "RECREATED"
		}
	}
}