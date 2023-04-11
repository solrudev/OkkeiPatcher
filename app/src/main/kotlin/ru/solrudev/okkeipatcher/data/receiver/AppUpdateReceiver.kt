/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MY_PACKAGE_REPLACED
import dagger.hilt.android.AndroidEntryPoint
import okio.FileSystem
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.repository.app.APP_UPDATE_FILE_NAME
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