package ru.solrudev.okkeipatcher.domain.usecase.work

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