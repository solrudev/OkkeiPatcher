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

package ru.solrudev.okkeipatcher.app.usecase.work

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class GetIsWorkPendingFlowUseCase @Inject constructor(
	private val getPendingWorkFlowUseCase: GetPendingWorkFlowUseCase,
	private val getWorkStateFlowUseCase: GetWorkStateFlowUseCase
) {

	@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
	operator fun invoke() = channelFlow {
		send(false)
		getPendingWorkFlowUseCase()
			.onEach { send(true) }
			.flatMapLatest { getWorkStateFlowUseCase(it) }
			.filter { it.isFinished }
			.onEach { send(false) }
			.collect()
	}
		.distinctUntilChanged()
		.debounce(500.milliseconds) // to skip sending first `false` if work is pending
}