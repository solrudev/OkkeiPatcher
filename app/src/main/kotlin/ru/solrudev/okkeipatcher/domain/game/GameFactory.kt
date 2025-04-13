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

import dagger.Reusable
import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.game.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.game.gamefile.Obb
import ru.solrudev.okkeipatcher.domain.game.gamefile.SaveData
import ru.solrudev.okkeipatcher.domain.operation.factory.ApkPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.operation.factory.ObbPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Inject

@Reusable
class GameFactory @Inject constructor(
	private val patchRepositoryFactory: SuspendFactory<PatchRepository>,
	private val apkPatchOperationFactory: ApkPatchOperationFactory,
	private val obbPatchOperationFactory: ObbPatchOperationFactory,
	private val apkRepository: ApkRepository,
	private val obbRepository: ObbRepository,
	private val apkBackupRepository: ApkBackupRepository,
	private val obbBackupRepository: ObbBackupRepository,
	private val saveData: SaveData
) : SuspendFactory<Game> {

	override suspend fun create(): Game {
		val patchRepository = patchRepositoryFactory.create()
		val apk = Apk(
			patchRepository.apkPatchFiles,
			apkPatchOperationFactory,
			apkRepository,
			apkBackupRepository
		)
		val obb = Obb(
			patchRepository.obbPatchFiles,
			obbPatchOperationFactory,
			obbRepository,
			obbBackupRepository
		)
		return Game(apk, obb, saveData)
	}
}