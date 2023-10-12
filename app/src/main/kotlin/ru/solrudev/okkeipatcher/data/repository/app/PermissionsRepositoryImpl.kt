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

package ru.solrudev.okkeipatcher.data.repository.app

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.app.model.Permission
import ru.solrudev.okkeipatcher.app.repository.PermissionsRepository
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_TREE_URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionsRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : PermissionsRepository {

	override fun getRequiredPermissions() = buildMap {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
			put(Permission.Storage, isStoragePermissionGranted())
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			put(Permission.Install, isInstallPermissionGranted())
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			put(Permission.Notifications, isNotificationsPermissionGranted())
		}
	}

	override fun isSaveDataAccessGranted(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
			return true
		}
		return applicationContext
			.contentResolver
			.persistedUriPermissions
			.any { it.uri == ANDROID_DATA_TREE_URI && it.isReadPermission && it.isWritePermission }
	}

	override fun isStoragePermissionGranted(): Boolean {
		val isReadStorageGranted =
			ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
		val isWriteStorageGranted =
			ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED
		return isReadStorageGranted && isWriteStorageGranted
	}

	override fun isInstallPermissionGranted(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return true
		}
		return applicationContext.packageManager.canRequestPackageInstalls()
	}

	override fun isNotificationsPermissionGranted(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
			return true
		}
		return ContextCompat.checkSelfPermission(applicationContext, POST_NOTIFICATIONS) == PERMISSION_GRANTED
	}
}