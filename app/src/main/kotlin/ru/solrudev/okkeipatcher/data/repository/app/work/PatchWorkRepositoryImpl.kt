package ru.solrudev.okkeipatcher.data.repository.app.work

import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.worker.PatchWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import javax.inject.Inject

private const val PATCH_WORK_NAME = "PatchWork"
private val workLabel = LocalizedString.resource(R.string.work_label_patch)

class PatchWorkRepositoryImpl @Inject constructor(
	workRepository: WorkRepository,
	workManager: WorkManager
) : PatchWorkRepository, UniqueWorkRepositoryImpl<PatchWorker>(
	workName = PATCH_WORK_NAME,
	workLabel = workLabel,
	workerClass = PatchWorker::class.java,
	workRepository,
	workManager
)