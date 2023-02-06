package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import ru.solrudev.okkeipatcher.app.model.License
import ru.solrudev.okkeipatcher.app.repository.LicensesRepository
import ru.solrudev.okkeipatcher.di.IoDispatcher
import javax.inject.Inject

private const val LICENSES_DIR = "licenses"

class LicensesRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LicensesRepository {

	override suspend fun getLicenses() = runInterruptible(ioDispatcher) {
		val assets = applicationContext.assets
		val licenses = assets.list(LICENSES_DIR) ?: return@runInterruptible emptyList()
		licenses
			.mapIndexed { index, license ->
				License(
					id = index,
					subject = license,
					text = assets
						.open("$LICENSES_DIR/$license")
						.reader()
						.use { it.readText() }
				)
			}
			.sortedBy { it.subject.lowercase() }
	}
}