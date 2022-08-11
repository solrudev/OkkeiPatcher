package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.service.operation.MockOperation

class MockOperationFactory(
	private val isPatchedDao: Persistable<Boolean>,
	private val tags: Set<String>
) : OperationFactory<Result> {

	override suspend fun create() = MockOperation(isPatchedDao, tags)
}