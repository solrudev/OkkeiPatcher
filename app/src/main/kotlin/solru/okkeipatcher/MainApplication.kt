package solru.okkeipatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.solrudev.simpleinstaller.SimpleInstaller
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.utils.Preferences
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	override fun onCreate() {
		super.onCreate()
		instance = this
		SimpleInstaller.initialize(this, R.mipmap.ic_launcher_foreground)
		setIsPatchedPreferenceIfNotSet()
		setCheckBoxStatePreferenceIfNotSet()
		setLanguagePreferenceIfNotSet()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationManager =
				getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(
				R.string.notification_channel_id,
				R.string.notification_channel_name,
				R.string.notification_channel_description,
				important = false
			)
		}
	}

	private fun setIsPatchedPreferenceIfNotSet() {
		if (Preferences.containsKey(AppKey.is_patched.name)) return
		Preferences.set(AppKey.is_patched.name, false)
	}

	private fun setCheckBoxStatePreferenceIfNotSet() {
		if (Preferences.containsKey(AppKey.process_save_data_enabled.name)) return
		Preferences.set(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
	}

	private fun setLanguagePreferenceIfNotSet() {
		if (Preferences.containsKey(AppKey.patch_language.name)) return
		Preferences.set(AppKey.patch_language.name, Language.English.name)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun NotificationManager.createNotificationChannel(
		channelId: Int,
		nameId: Int,
		descriptionId: Int,
		important: Boolean
	) {
		val channelIdString = getString(channelId)
		val channelName = getString(nameId)
		val channelDescription = getString(descriptionId)
		val importance = NotificationManager.IMPORTANCE_HIGH
		val channel = NotificationChannel(channelIdString, channelName, importance).apply {
			description = channelDescription
			if (!important) setSound(null, null)
		}
		createNotificationChannel(channel)
	}

	override fun getWorkManagerConfiguration() =
		Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.build()

	companion object {
		private lateinit var instance: MainApplication
		val context: Context get() = instance.applicationContext
	}
}
