package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import javax.inject.Inject

private const val TWO_GB: Long = 2_147_483_648

class StorageCheckerImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : StorageChecker {

	override fun isEnoughSpace() = applicationContext.externalDir.usableSpace >= TWO_GB
}