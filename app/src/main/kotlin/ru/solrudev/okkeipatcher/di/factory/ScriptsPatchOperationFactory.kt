package ru.solrudev.okkeipatcher.di.factory

import dagger.assisted.AssistedFactory
import ru.solrudev.okkeipatcher.domain.repository.patch.ScriptsDataRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.service.operation.ScriptsPatchOperation

@AssistedFactory
interface ScriptsPatchOperationFactory {
	fun create(apk: Apk, scriptsDataRepository: ScriptsDataRepository): ScriptsPatchOperation
}