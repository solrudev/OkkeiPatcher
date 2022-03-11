package solru.okkeipatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.solrudev.simpleinstaller.SimpleInstaller
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.model.Language
import solru.okkeipatcher.utils.Preferences
import javax.inject.Inject

@HiltAndroidApp
class OkkeiApplication : Application(), Configuration.Provider {

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	override fun onCreate() {
		super.onCreate()
		instance = this
		connectivityManager = getSystemService()
		SimpleInstaller.initialize(this, R.mipmap.ic_launcher_foreground)
		initIsPatchedPreference()
		initSaveDataPreference()
		initLanguagePreference()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationManager = getSystemService<NotificationManager>()
			notificationManager?.createNotificationChannel(
				R.string.notification_channel_progress_id,
				R.string.notification_channel_progress_name,
				R.string.notification_channel_progress_description
			)
			notificationManager?.createNotificationChannel(
				R.string.notification_channel_messages_id,
				R.string.notification_channel_messages_name,
				R.string.notification_channel_messages_description
			)
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			startNetworkMonitoring()
		}
	}

	private fun initIsPatchedPreference() {
		if (Preferences.containsKey(AppKey.is_patched.name)) return
		Preferences.set(AppKey.is_patched.name, false)
	}

	private fun initSaveDataPreference() {
		if (Preferences.containsKey(AppKey.process_save_data_enabled.name)) return
		Preferences.set(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
	}

	private fun initLanguagePreference() {
		if (Preferences.containsKey(AppKey.patch_language.name)) return
		Preferences.set(AppKey.patch_language.name, Language.English.name)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun NotificationManager.createNotificationChannel(
		channelId: Int,
		nameId: Int,
		descriptionId: Int
	) {
		val channelIdString = getString(channelId)
		val channelName = getString(nameId)
		val channelDescription = getString(descriptionId)
		val importance = NotificationManager.IMPORTANCE_DEFAULT
		val channel = NotificationChannel(channelIdString, channelName, importance).apply {
			description = channelDescription
		}
		createNotificationChannel(channel)
	}

	override fun getWorkManagerConfiguration() = Configuration.Builder()
		.setWorkerFactory(workerFactory)
		.build()

	companion object {

		val context: Context
			get() = instance.applicationContext

		val isNetworkAvailable: Boolean
			get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				_isNetworkAvailable
			} else {
				isActiveNetworkConnected()
			}

		private lateinit var instance: OkkeiApplication
		private var connectivityManager: ConnectivityManager? = null
		private var _isNetworkAvailable = false

		@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
		private fun startNetworkMonitoring() {
			val networkRequest = NetworkRequest.Builder().apply {
				addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
				}
				addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
				addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
				addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
			}.build()
			val networkCallback = object : ConnectivityManager.NetworkCallback() {

				override fun onAvailable(network: Network) {
					_isNetworkAvailable = true
				}

				override fun onLost(network: Network) {
					_isNetworkAvailable = false
				}
			}
			connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
		}

		@Suppress("DEPRECATION")
		private fun isActiveNetworkConnected(): Boolean {
			val activeNetworkInfo = connectivityManager?.activeNetworkInfo
			return activeNetworkInfo != null && activeNetworkInfo.isConnected
		}
	}
}
