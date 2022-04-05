package ru.solrudev.okkeipatcher.domain.operation.extension

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.runningReduce
import ru.solrudev.okkeipatcher.domain.operation.Operation

fun <T> Operation<T>.statusAndAccumulatedProgress() = status
	.conflate()
	.combine(
		progressDelta
			.runningReduce { accumulator, value -> accumulator + value }
			.conflate()
	) { status, progress -> status to progress }
	.conflate()