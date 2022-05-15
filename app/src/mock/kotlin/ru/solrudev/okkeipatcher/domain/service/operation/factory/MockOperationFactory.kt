package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.service.operation.MockOperation

class MockOperationFactory(
	private val isPatchedDao: Dao<Boolean>,
	private val tags: Set<String>
) : OperationFactory<Unit> {

	override suspend fun create() = MockOperation(isPatchedDao, tags)
}