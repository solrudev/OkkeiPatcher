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

package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import java.io.IOException
import javax.inject.Inject

private const val REQUIRED_ALLOCATABLE_BYTES = 5_905_580_032L

@Reusable
class StorageCheckerImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : StorageChecker {

	override fun isEnoughSpace(): Boolean {
		val storageManager = applicationContext.getSystemService<StorageManager>()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && storageManager != null) {
			return useStorageManager(storageManager)
		}
		return isEnoughUsableSpace()
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun useStorageManager(storageManager: StorageManager): Boolean {
		try {
			val storageUuid = storageManager.getUuidForPath(applicationContext.externalDir)
			val allocatableBytes = storageManager.getAllocatableBytes(storageUuid)
			if (allocatableBytes < REQUIRED_ALLOCATABLE_BYTES) {
				return false
			}
			storageManager.allocateBytes(storageUuid, REQUIRED_ALLOCATABLE_BYTES)
			return true
		} catch (_: IOException) {
			return isEnoughUsableSpace()
		}
	}

	private fun isEnoughUsableSpace(): Boolean {
		return applicationContext.externalDir.usableSpace >= REQUIRED_ALLOCATABLE_BYTES
	}
}