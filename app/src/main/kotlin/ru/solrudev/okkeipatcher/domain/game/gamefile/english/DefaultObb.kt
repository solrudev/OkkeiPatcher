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

package ru.solrudev.okkeipatcher.domain.game.gamefile.english

import ru.solrudev.okkeipatcher.domain.game.gamefile.Obb
import ru.solrudev.okkeipatcher.domain.operation.factory.ObbPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbPatchOperationFactory: ObbPatchOperationFactory,
	obbRepository: ObbRepository,
	obbBackupRepository: ObbBackupRepository
) : Obb(obbRepository, obbBackupRepository) {

	private val obbPatchOperation = obbPatchOperationFactory.create(patchRepository.obbPatchFiles)

	override fun patch() = obbPatchOperation
	override fun update() = patch()
}