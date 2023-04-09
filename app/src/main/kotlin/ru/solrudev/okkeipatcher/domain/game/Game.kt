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

import ru.solrudev.okkeipatcher.domain.game.gamefile.GameFile
import ru.solrudev.okkeipatcher.domain.game.gamefile.PatchableGameFile

interface Game : PatchableGame, BackupableGame {

	override fun close() {
		apk.close()
		obb.close()
		saveData.close()
	}
}

interface PatchableGame : AutoCloseable {
	val apk: PatchableGameFile
	val obb: PatchableGameFile
	val saveData: GameFile
}

interface BackupableGame : AutoCloseable {
	val apk: GameFile
	val obb: GameFile
	val saveData: GameFile
}