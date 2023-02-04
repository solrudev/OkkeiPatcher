package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Inject
import javax.inject.Provider

class ClearDataUseCase @Inject constructor(
	private val apkRepository: ApkRepository,
	private val apkBackupRepository: ApkBackupRepository,
	private val obbBackupRepository: ObbBackupRepository,
	private val saveDataRepository: SaveDataRepository,
	private val preferencesRepository: PreferencesRepository,
	private val hashRepository: HashRepository,
	private val patchRepositories: Map<Language, @JvmSuppressWildcards Provider<PatchRepository>>
) {

	suspend operator fun invoke() = try {
		apkRepository.deleteTemp()
		apkBackupRepository.deleteBackup()
		obbBackupRepository.deleteBackup()
		saveDataRepository.deleteBackup()
		preferencesRepository.reset()
		hashRepository.clear()
		patchRepositories.values.forEach {
			it.get().clearPersistedData()
		}
		Result.success()
	} catch (_: Throwable) {
		Result.failure(R.string.error_clear_data)
	}
}