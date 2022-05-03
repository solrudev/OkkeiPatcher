package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.operation.Operation

interface OperationFactory<out R> {
	suspend fun create(): Operation<R>
}