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

package ru.solrudev.okkeipatcher.data.network.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.solrudev.okkeipatcher.data.network.model.FileDto
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherVersionDto

interface OkkeiPatcherApi {

	@GET("app")
	suspend fun getOkkeiPatcherData(): FileDto

	@GET("app/changelog")
	suspend fun getChangelog(
		@Query("version") currentVersion: Int,
		@Header("Accept-Language") language: String
	): List<OkkeiPatcherVersionDto>
}