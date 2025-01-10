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

package ru.solrudev.okkeipatcher.patch.english.data

import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.data.repository.patch.PatchRepositoryImpl
import ru.solrudev.okkeipatcher.patch.english.domain.DefaultPatchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPatchRepositoryImpl @Inject constructor(
	defaultPatchApi: DefaultPatchApi,
	preferencesRepository: PreferencesRepository,
	preferencesDataStoreFactory: PreferencesDataStoreFactory
) : PatchRepositoryImpl(
	defaultPatchApi,
	preferencesRepository,
	preferencesDataStoreFactory,
	dataStoreName = "patch_files_en"
), DefaultPatchRepository