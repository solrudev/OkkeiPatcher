package ru.solrudev.okkeipatcher.data.repository.app.work

import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.worker.RestoreWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import javax.inject.Inject

private const val RESTORE_WORK_NAME = "RestoreWork"
private val workLabel = LocalizedString.resource(R.string.work_label_restoring)

class RestoreWorkRepositoryImpl @Inject constructor(
	workRepository: WorkRepository,
	workManager: WorkManager
) : RestoreWorkRepository, ConcreteWorkRepositoryImpl<RestoreWorker>(
	workName = RESTORE_WORK_NAME,
	workLabel = workLabel,
	workerClass = RestoreWorker::class.java,
	workRepository,
	workManager
)