package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.worker.DownloadUpdateWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.repository.work.DownloadUpdateWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

private const val DOWNLOAD_UPDATE_WORK_NAME = "DownloadAppUpdateWork"
private val workLabel = LocalizedString.resource(R.string.work_label_download_update)

class DownloadUpdateWorkRepositoryImpl @Inject constructor(
	workRepository: WorkRepository,
	workManager: WorkManager
) : DownloadUpdateWorkRepository, ConcreteWorkRepositoryImpl<DownloadUpdateWorker>(
	workName = DOWNLOAD_UPDATE_WORK_NAME,
	workLabel = workLabel,
	workerClass = DownloadUpdateWorker::class.java,
	workRepository,
	workManager
)