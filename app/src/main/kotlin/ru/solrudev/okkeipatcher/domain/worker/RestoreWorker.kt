package ru.solrudev.okkeipatcher.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import ru.solrudev.okkeipatcher.domain.service.operation.RestoreOperation
import javax.inject.Provider

@HiltWorker
class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val preferencesRepository: PreferencesRepository,
	private val strategies: Map<Language, @JvmSuppressWildcards Provider<GameFileStrategy>>
) : ForegroundWorker(context, workerParameters) {

	override val progressNotificationTitle = LocalizedString.resource(R.string.notification_title_restore)

	override suspend fun getOperation(): Operation<Unit> {
		val handleSaveData = preferencesRepository.getHandleSaveData()
		val patchLanguage = preferencesRepository.getPatchLanguage()
		val strategy = strategies.getValue(patchLanguage).get()
		val restoreOperation = RestoreOperation(strategy, handleSaveData, preferencesRepository)
		restoreOperation.checkCanRestore()
		return restoreOperation
	}

	override fun createPendingIntent() = NavDeepLinkBuilder(applicationContext)
		.setGraph(R.navigation.okkei_nav_graph)
		.setDestination(R.id.restore_fragment)
		.createPendingIntent()
}