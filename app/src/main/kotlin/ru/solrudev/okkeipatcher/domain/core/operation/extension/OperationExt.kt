package ru.solrudev.okkeipatcher.domain.core.operation.extension

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.runningReduce
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.operation.Operation

fun <T> Operation<T>.statusAndAccumulatedProgress() = status
	.onStart { emit(LocalizedString.empty()) }
	.conflate()
	.combine(
		progressDelta
			.onStart { emit(0) }
			.runningReduce { accumulator, value -> accumulator + value }
			.conflate()
	) { status, progress -> status to progress }
	.conflate()