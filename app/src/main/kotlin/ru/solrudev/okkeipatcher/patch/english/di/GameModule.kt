/*
 * Okkei Patcher
 * Copyright (C) 2023-2025 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.patch.english.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.di.LanguageKey
import ru.solrudev.okkeipatcher.domain.game.Game
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.patch.english.domain.DefaultGame

@InstallIn(SingletonComponent::class)
@Module
interface GameModule {

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun bindDefaultGame(defaultGame: DefaultGame): Game
}