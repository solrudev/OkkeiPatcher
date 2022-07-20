package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Inject
import javax.inject.Provider

interface ClearDataUseCase {
	suspend operator fun invoke(): Result
}

class ClearDataUseCaseImpl @Inject constructor(
	private val apkRepository: ApkRepository,
	private val obbRepository: ObbRepository,
	private val saveDataRepository: SaveDataRepository,
	private val preferencesRepository: PreferencesRepository,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	private val patchRepositories: Map<Language, @JvmSuppressWildcards Provider<PatchRepository>>
) : ClearDataUseCase {

	override suspend fun invoke() = try {
		apkRepository.backupApk.delete()
		apkRepository.tempApk.delete()
		obbRepository.deleteBackup()
		saveDataRepository.deleteBackup()
		preferencesRepository.reset()
		commonFilesHashRepository.clear()
		patchRepositories.values.forEach {
			it.get().clearPersistedData()
		}
		Result.Success
	} catch (_: Throwable) {
		Result.Failure(LocalizedString.resource(R.string.error_clear_data))
	}
}