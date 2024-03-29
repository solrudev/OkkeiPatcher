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

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.game.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.operation.factory.ScriptsPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.updateInstalledVersion
import javax.inject.Inject

class DefaultApk @Inject constructor(
	patchRepository: DefaultPatchRepository,
	scriptsPatchOperationFactory: ScriptsPatchOperationFactory,
	private val apkRepository: ApkRepository,
	apkBackupRepository: ApkBackupRepository
) : Apk(apkRepository, apkBackupRepository) {

	private val scripts = patchRepository.scripts
	private val scriptsPatchOperation = scriptsPatchOperationFactory.create(scripts)

	override fun patch() = patch(updating = false)
	override fun update() = patch(updating = true)

	private fun patch(updating: Boolean): Operation<Unit> {
		val installPatchedOperation = installPatched(updating)
		return operation(scriptsPatchOperation, installPatchedOperation) {
			status(R.string.status_comparing_apk)
			if (apkRepository.verifyTemp()) {
				progressDelta(scriptsPatchOperation.progressMax)
				installPatchedOperation()
				scripts.updateInstalledVersion()
				return@operation
			}
			apkRepository.deleteTemp()
			scriptsPatchOperation()
			installPatchedOperation()
			scripts.updateInstalledVersion()
		}
	}
}