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

package ru.solrudev.okkeipatcher.ui.shared.model

import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.model.WorkState
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

interface HasWork {
	val work: Work
}

interface WorkStateEventFactory<E : JetEvent> {

	fun running(status: LocalizedString, progressData: ProgressData): E
	fun failed(reason: LocalizedString, stackTrace: String): E
	fun succeeded(): E
	fun canceled(): E
	fun unknown(): E

	fun fromWorkState(workState: WorkState) = when (workState) {
		is WorkState.Running -> running(workState.status, workState.progressData)
		is WorkState.Failed -> failed(workState.reason, workState.stackTrace)
		is WorkState.Succeeded -> succeeded()
		is WorkState.Canceled -> canceled()
		is WorkState.Unknown -> unknown()
	}
}