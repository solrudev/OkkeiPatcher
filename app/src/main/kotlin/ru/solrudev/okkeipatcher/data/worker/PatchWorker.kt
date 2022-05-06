package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.service.operation.factory.PatchOperationFactory

private val workLabel = LocalizedString.resource(R.string.notification_title_patch)

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	patchOperationFactory: PatchOperationFactory
) : ForegroundWorker(context, workerParameters, patchOperationFactory, workLabel)