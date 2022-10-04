package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.service.operation.MockOperation

class MockOperationFactory(
	private val patchVersion: Persistable<String>,
	private val patchStatus: Persistable<Boolean>,
	private val isPatchWork: Boolean
) : OperationFactory<Result> {

	override suspend fun create() = MockOperation(patchVersion, patchStatus, isPatchWork)
}