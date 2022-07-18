package ru.solrudev.okkeipatcher.data.repository.app

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_TREE_URI
import ru.solrudev.okkeipatcher.domain.model.Permission
import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import javax.inject.Inject

class PermissionsRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : PermissionsRepository {

	override fun getRequiredPermissions() = buildMap {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			put(Permission.Storage, isStoragePermissionGranted())
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			put(Permission.Install, isInstallPermissionGranted())
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
		val isReadStorageGranted = ContextCompat.checkSelfPermission(
			applicationContext,
			READ_EXTERNAL_STORAGE
		) == PERMISSION_GRANTED
		val isWriteStorageGranted = ContextCompat.checkSelfPermission(
			applicationContext,
			WRITE_EXTERNAL_STORAGE
		) == PERMISSION_GRANTED
		return isReadStorageGranted && isWriteStorageGranted
	}

	override fun isInstallPermissionGranted(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return true
		}
		return applicationContext.packageManager.canRequestPackageInstalls()
	}
}