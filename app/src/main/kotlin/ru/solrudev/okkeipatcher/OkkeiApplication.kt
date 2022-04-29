package ru.solrudev.okkeipatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.solrudev.simpleinstaller.SimpleInstaller
import ru.solrudev.okkeipatcher.domain.repository.app.ConnectivityRepository
import javax.inject.Inject

@HiltAndroidApp
class OkkeiApplication : Application(), Configuration.Provider {

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	@Inject
	lateinit var connectivityRepository: ConnectivityRepository

	override fun onCreate() {
		super.onCreate()
		instance = this
		SimpleInstaller.initialize(this, R.mipmap.ic_launcher_foreground)
		connectivityRepository.startNetworkMonitoring()
		createNotificationChannels()
	}

	private fun createNotificationChannels() {
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
	}

	override fun getWorkManagerConfiguration() = Configuration.Builder()
		.setWorkerFactory(workerFactory)
		.build()

	@RequiresApi(Build.VERSION_CODES.O)
	private fun NotificationManager.createNotificationChannel(
		@StringRes channelId: Int,
		@StringRes nameId: Int,
		@StringRes descriptionId: Int
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

	override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
		super.onConfigurationChanged(newConfig)
		createNotificationChannels()
	}

	companion object {

		val context: Context
			get() = instance.applicationContext

		private lateinit var instance: OkkeiApplication
	}
}