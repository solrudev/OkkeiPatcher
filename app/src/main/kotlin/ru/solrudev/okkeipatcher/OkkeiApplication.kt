package ru.solrudev.okkeipatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.solrudev.okkeipatcher.app.model.Theme
import ru.solrudev.okkeipatcher.app.usecase.GetThemeFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.StartNetworkMonitoringUseCase
import javax.inject.Inject

@HiltAndroidApp
class OkkeiApplication : Application(), Configuration.Provider {

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	@Inject
	lateinit var startNetworkMonitoringUseCase: StartNetworkMonitoringUseCase

	@Inject
	lateinit var getThemeFlowUseCase: GetThemeFlowUseCase

	override fun onCreate() {
		super.onCreate()
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
		initTheme()
		startNetworkMonitoringUseCase()
		createNotificationChannels()
	}

	override fun getWorkManagerConfiguration() = Configuration.Builder()
		.setWorkerFactory(workerFactory)
		.build()

	override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
		super.onConfigurationChanged(newConfig)
		createNotificationChannels()
	}

	fun setTheme(theme: Theme) {
		AppCompatDelegate.setDefaultNightMode(theme.toNightMode())
	}

	private fun initTheme() = runBlocking {
		val theme = getThemeFlowUseCase().first()
		setTheme(theme)
	}

	private fun Theme.toNightMode() = when (this) {
		Theme.FollowSystem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
		Theme.Light -> AppCompatDelegate.MODE_NIGHT_NO
		Theme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
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
}