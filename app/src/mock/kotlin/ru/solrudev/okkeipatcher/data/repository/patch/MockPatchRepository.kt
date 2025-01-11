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

package ru.solrudev.okkeipatcher.data.repository.patch

import ru.solrudev.okkeipatcher.data.network.api.MockPatchApi
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.data.service.MockGameInstallationProvider
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockPatchRepository @Inject constructor(
	patchApi: MockPatchApi,
	patchStateRepository: PatchStateRepository,
	gameInstallationProvider: MockGameInstallationProvider,
	preferencesDataStoreFactory: PreferencesDataStoreFactory
) : PatchRepositoryImpl(
	patchApi,
	patchStateRepository,
	gameInstallationProvider,
	preferencesDataStoreFactory,
	dataStoreName = "patch_files_mock"
)