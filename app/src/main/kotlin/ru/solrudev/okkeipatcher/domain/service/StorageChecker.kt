package ru.solrudev.okkeipatcher.domain.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.domain.isEnoughSpace
import javax.inject.Inject

interface StorageChecker {
	fun isEnoughSpace(): Boolean
}

class StorageCheckerImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : StorageChecker {

	override fun isEnoughSpace() = applicationContext.isEnoughSpace
}