package ru.solrudev.okkeipatcher.data.repository.app.work

import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.repository.work.DownloadUpdateWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.worker.DownloadUpdateWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import javax.inject.Inject

private const val DOWNLOAD_UPDATE_WORK_NAME = "DownloadAppUpdateWork"
private val workLabel = LocalizedString.resource(R.string.work_label_download_update)

class DownloadUpdateWorkRepositoryImpl @Inject constructor(
	workRepository: WorkRepository,
	workManager: WorkManager
) : DownloadUpdateWorkRepository, UniqueWorkRepositoryImpl<DownloadUpdateWorker>(
	workName = DOWNLOAD_UPDATE_WORK_NAME,
	workLabel = workLabel,
	workerClass = DownloadUpdateWorker::class.java,
	workRepository,
	workManager
)