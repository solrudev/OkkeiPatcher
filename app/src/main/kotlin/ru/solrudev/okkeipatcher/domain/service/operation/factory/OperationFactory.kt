package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.factory.Factory
import ru.solrudev.okkeipatcher.domain.operation.Operation

interface OperationFactory<out R> : Factory<Operation<R>>