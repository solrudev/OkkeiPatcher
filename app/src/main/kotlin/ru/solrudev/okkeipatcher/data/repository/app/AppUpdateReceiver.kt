package ru.solrudev.okkeipatcher.data.repository.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MY_PACKAGE_REPLACED
import dagger.hilt.android.AndroidEntryPoint
import okio.FileSystem
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import javax.inject.Inject

@AndroidEntryPoint
class AppUpdateReceiver : BroadcastReceiver() {

	@Inject
	lateinit var environment: OkkeiEnvironment

	@Inject
	lateinit var fileSystem: FileSystem

	override fun onReceive(context: Context?, intent: Intent?) {
		if (intent?.action != ACTION_MY_PACKAGE_REPLACED) {
			return
		}
		val updateFile = environment.externalFilesPath / APP_UPDATE_FILE_NAME
		fileSystem.delete(updateFile)
		println("AppUpdateReceiver: deleted update APK")
	}
}