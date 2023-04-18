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

@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.service.apksigner.ApkSignerApi19
import ru.solrudev.okkeipatcher.data.service.apksigner.ApkSignerApi24
import ru.solrudev.okkeipatcher.data.service.apksigner.ApkSignerImplementation
import javax.inject.Provider

@InstallIn(SingletonComponent::class)
@Module
object ApkSignerImplementationModule {

	@Provides
	fun provideApkSignerImplementation(
		apkSignerApi24: Provider<ApkSignerApi24>,
		apkSignerApi19: Provider<ApkSignerApi19>
	): ApkSignerImplementation {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return apkSignerApi24.get()
		}
		return apkSignerApi19.get()
	}
}