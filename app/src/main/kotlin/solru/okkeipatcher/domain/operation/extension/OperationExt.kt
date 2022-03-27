package solru.okkeipatcher.domain.operation.extension

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.runningReduce
import solru.okkeipatcher.domain.operation.Operation

fun <T> Operation<T>.statusAndAccumulatedProgress() = status
	.conflate()
	.combine(
		progressDelta
			.runningReduce { accumulator, value -> accumulator + value }
			.conflate()
	) { status, progress -> status to progress }
	.conflate()