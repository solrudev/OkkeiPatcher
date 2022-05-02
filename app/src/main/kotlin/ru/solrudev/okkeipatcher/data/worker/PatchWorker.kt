package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import ru.solrudev.okkeipatcher.domain.service.operation.PatchOperation
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import javax.inject.Provider

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val preferencesRepository: PreferencesRepository,
	private val getPatchUpdatesUseCases: Map<Language, @JvmSuppressWildcards Provider<GetPatchUpdatesUseCase>>,
	private val strategies: Map<Language, @JvmSuppressWildcards Provider<GameFileStrategy>>
) : ForegroundWorker(context, workerParameters) {

	override val workTitle = LocalizedString.resource(R.string.notification_title_patch)
	override val destinationScreen = R.id.patch_fragment

	override suspend fun getOperation(): Operation<Unit> {
		val handleSaveData = preferencesRepository.handleSaveDataDao.retrieve()
		val patchLanguage = preferencesRepository.patchLanguageDao.retrieve()
		val getPatchUpdatesUseCase = getPatchUpdatesUseCases.getValue(patchLanguage).get()
		val patchUpdates = getPatchUpdatesUseCase()
		val strategy = strategies.getValue(patchLanguage).get()
		val patchOperation = PatchOperation(strategy, handleSaveData, patchUpdates, preferencesRepository.isPatchedDao)
		patchOperation.checkCanPatch()
		return patchOperation
	}
}