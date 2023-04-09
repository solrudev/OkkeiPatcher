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

package ru.solrudev.okkeipatcher.domain.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.game.GameFactory
import ru.solrudev.okkeipatcher.domain.model.PatchParameters
import ru.solrudev.okkeipatcher.domain.operation.PatchOperation
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import javax.inject.Inject

class PatchOperationFactory @Inject constructor(
	private val patchStateRepository: PatchStateRepository,
	private val patchRepositoryFactory: PatchRepositoryFactory,
	private val gameFactory: GameFactory,
	private val storageChecker: StorageChecker
) : OperationFactory<Result> {

	override suspend fun create(): Operation<Result> {
		val game = gameFactory.create()
		val handleSaveData = patchStateRepository.handleSaveData.retrieve()
		val patchRepository = patchRepositoryFactory.create()
		val patchUpdates = patchRepository.getPatchUpdates()
		val patchVersion = patchRepository.getDisplayVersion()
		val parameters = PatchParameters(handleSaveData, patchUpdates, patchVersion)
		return PatchOperation(
			parameters,
			game,
			patchStateRepository.patchVersion,
			patchStateRepository.patchStatus,
			storageChecker
		)
	}
}