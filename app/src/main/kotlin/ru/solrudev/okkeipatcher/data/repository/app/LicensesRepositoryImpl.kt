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