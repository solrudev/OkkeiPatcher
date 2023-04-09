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

package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Inject
import javax.inject.Provider

class ClearDataUseCase @Inject constructor(
	private val apkRepository: ApkRepository,
	private val apkBackupRepository: ApkBackupRepository,
	private val obbBackupRepository: ObbBackupRepository,
	private val saveDataRepository: SaveDataRepository,
	private val preferencesRepository: PreferencesRepository,
	private val hashRepository: HashRepository,
	private val patchRepositories: Map<Language, @JvmSuppressWildcards Provider<PatchRepository>>
) {

	suspend operator fun invoke() = try {
		apkRepository.deleteTemp()
		apkBackupRepository.deleteBackup()
		obbBackupRepository.deleteBackup()
		saveDataRepository.deleteBackup()
		preferencesRepository.reset()
		hashRepository.clear()
		patchRepositories.values.forEach {
			it.get().clearPersistedData()
		}
		Result.success()
	} catch (_: Throwable) {
		Result.failure(R.string.error_clear_data)
	}
}