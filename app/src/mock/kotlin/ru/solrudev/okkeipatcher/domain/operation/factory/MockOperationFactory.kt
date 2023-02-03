package ru.solrudev.okkeipatcher.domain.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.operation.MockOperation

class MockOperationFactory(
	private val patchRepositoryFactory: PatchRepositoryFactory,
	private val patchVersion: Persistable<String>,
	private val patchStatus: Persistable<Boolean>,
	private val isPatchWork: Boolean
) : OperationFactory<Result> {

	override suspend fun create(): Operation<Result> {
		val patchRepository = patchRepositoryFactory.create()
		return MockOperation(patchRepository, patchVersion, patchStatus, isPatchWork)
	}
}