package ru.solrudev.okkeipatcher.domain.operation.factory

import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.core.operation.Operation

interface OperationFactory<out R> : SuspendFactory<Operation<R>>