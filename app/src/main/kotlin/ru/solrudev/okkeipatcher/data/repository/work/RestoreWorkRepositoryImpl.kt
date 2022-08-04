package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.worker.RestoreWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
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