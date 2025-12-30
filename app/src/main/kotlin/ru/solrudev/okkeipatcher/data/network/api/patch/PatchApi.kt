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

package ru.solrudev.okkeipatcher.data.network.api.patch

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Retrofit
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.network.model.patch.PatchRequestDto
import ru.solrudev.okkeipatcher.data.network.model.patch.PatchResponseDto
import ru.solrudev.okkeipatcher.di.DefaultDispatcher
import javax.inject.Inject
import javax.inject.Singleton

interface PatchApi {
	suspend fun getPatchData(patchRequestDto: PatchRequestDto): PatchResponseDto
}

suspend inline fun PatchApi.getPatchData(gameVersion: Int?) = getPatchData(PatchRequestDto(gameVersion))

@Singleton
class ApiFlowFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val retrofitBuilder: Retrofit.Builder,
	@DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {

	private val coroutineScope = CoroutineScope(defaultDispatcher)
	private val mutex = Mutex()

	fun <T> create(apiClass: Class<T>) = preferencesRepository
		.apiUrl
		.flow
		.distinctUntilChanged()
		.map { apiUrl ->
			mutex.withLock {
				retrofitBuilder
					.baseUrl(apiUrl)
					.build()
					.create(apiClass)
			}
		}
		.stateIn(coroutineScope, SharingStarted.Lazily, initialValue = null)
		.filterNotNull()

	inline fun <reified T> create() = create(T::class.java)
}