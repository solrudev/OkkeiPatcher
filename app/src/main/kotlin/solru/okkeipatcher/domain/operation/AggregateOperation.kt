package solru.okkeipatcher.domain.operation

import kotlinx.coroutines.flow.merge

/**
 * Aggregates other operations and executes them one by one sequentially.
 */
open class AggregateOperation(private val operations: List<Operation<*>>) : Operation<Unit> {

	final override val status = operations
		.map { it.status }
		.merge()

	final override val messages = operations
		.map { it.messages }
		.merge()

	final override val progressDelta = operations
		.map { it.progressDelta }
		.merge()

	final override val progressMax = operations.sumOf { it.progressMax }

	final override suspend fun invoke() {
		preInvoke()
		operations.forEach { it.invoke() }
		postInvoke()
	}

	protected open suspend fun preInvoke() {}
	protected open suspend fun postInvoke() {}
}