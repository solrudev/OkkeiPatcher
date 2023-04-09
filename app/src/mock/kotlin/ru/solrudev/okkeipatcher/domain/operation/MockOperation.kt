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

package ru.solrudev.okkeipatcher.domain.operation

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import kotlin.time.Duration.Companion.seconds

private const val STEPS_COUNT = 5

@Suppress("FunctionName")
fun MockOperation(
	patchRepository: PatchRepository,
	patchVersion: Persistable<String>,
	patchStatus: Persistable<Boolean>,
	isPatchWork: Boolean
) = operation(progressMax = STEPS_COUNT * 100) {
	repeat(STEPS_COUNT) { stepIndex ->
		val step = stepIndex + 1
		status(step.toString().repeat(10))
		delay(1.seconds)
		progressDelta(100)
	}
	if (isPatchWork) {
		patchVersion.persist(patchRepository.getDisplayVersion())
	} else {
		patchVersion.clear()
	}
	patchStatus.persist(isPatchWork)
	Result.success()
}