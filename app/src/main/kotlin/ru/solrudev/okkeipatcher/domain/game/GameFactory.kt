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

package ru.solrudev.okkeipatcher.domain.game

import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import javax.inject.Inject
import javax.inject.Provider

class GameFactory @Inject constructor(
	private val patchStateRepository: PatchStateRepository,
	private val games: Map<Language, @JvmSuppressWildcards Provider<Game>>
) : SuspendFactory<Game> {

	override suspend fun create(): Game {
		val patchLanguage = patchStateRepository.patchLanguage.retrieve()
		return games.getValue(patchLanguage).get()
	}
}