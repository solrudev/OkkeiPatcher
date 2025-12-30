/*
 * Okkei Patcher
 * Copyright (C) 2024 Ilya Fomichev
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

@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.api.patch.ApiFlowFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkFlavoredModule {

	@Provides
	@Singleton
	fun provideOkkeiPatcherApi(apiFlowFactory: ApiFlowFactory): Flow<@JvmWildcard OkkeiPatcherApi> {
		return apiFlowFactory.create<OkkeiPatcherApi>()
	}
}