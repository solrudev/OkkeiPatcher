/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.repository.patch.factory

import ru.solrudev.okkeipatcher.data.network.api.patch.PatchApi
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.data.repository.patch.PatchRepositoryImpl
import ru.solrudev.okkeipatcher.data.service.GameInstallationProvider
import ru.solrudev.okkeipatcher.data.util.computeIfAbsentCompat
import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoriesProvider
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class PatchRepositoryFactory @Inject constructor(
	private val patchStateRepository: PatchStateRepository,
	private val patchApis: Map<Language, @JvmSuppressWildcards Provider<PatchApi>>,
	private val gameInstallationProvider: GameInstallationProvider,
	private val preferencesDataStoreFactory: PreferencesDataStoreFactory
) : SuspendFactory<PatchRepository>, PatchRepositoriesProvider {

	private val cache = ConcurrentHashMap<Language, PatchRepository>()

	override suspend fun create(): PatchRepository {
		val patchLanguage = patchStateRepository.patchLanguage.retrieve()
		return create(patchLanguage)
	}

	override fun get() = Language.entries.associate { language ->
		language to Provider {
			create(language)
		}
	}

	private fun create(patchLanguage: Language) = cache.computeIfAbsentCompat(patchLanguage) { language ->
		val patchApi = patchApis.getValue(language).get()
		PatchRepositoryImpl(
			patchApi,
			patchStateRepository,
			gameInstallationProvider,
			preferencesDataStoreFactory,
			dataStoreName = "patch_files_${language.shortName}"
		)
	}
}