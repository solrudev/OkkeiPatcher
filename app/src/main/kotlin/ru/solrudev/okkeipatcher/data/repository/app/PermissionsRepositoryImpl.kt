package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_TREE_URI
import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import javax.inject.Inject

class PermissionsRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : PermissionsRepository {

	override fun isSaveDataAccessGranted(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
			return true
		}
		return applicationContext
			.contentResolver
			.persistedUriPermissions
			.any { it.uri == ANDROID_DATA_TREE_URI && it.isReadPermission && it.isWritePermission }
	}
}