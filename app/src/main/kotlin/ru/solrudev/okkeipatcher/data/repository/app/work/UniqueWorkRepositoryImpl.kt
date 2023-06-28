/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.repository.app.work

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.lifecycle.asFlow
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.repository.work.UniqueWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.repository.app.work.mapper.toWork
import ru.solrudev.okkeipatcher.data.worker.ForegroundOperationWorker
import ru.solrudev.okkeipatcher.data.worker.util.extension.getSerializable
import ru.solrudev.okkeipatcher.data.worker.util.extension.putSerializable
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.screen.work.WorkFragmentArgs

private const val WORK_LABEL_KEY = "WORK_LABEL"

open class UniqueWorkRepositoryImpl<T : ForegroundOperationWorker>(
	private val workName: String,
	private val workLabel: LocalizedString,
	private val workerClass: Class<T>,
	private val workRepository: WorkRepository,
	private val workManager: WorkManager
) : UniqueWorkRepository {

	override suspend fun enqueueWork(): Work {
		workManager.getWorkInfosByTag(workName).await()
			.firstOrNull { workInfo -> !workInfo.state.isFinished }
			?.let { workInfo -> return Work(workInfo.id, workLabel) }
		val workRequest = OneTimeWorkRequest.Builder(workerClass)
			.setInputData(Data.Builder().putSerializable(WORK_LABEL_KEY, workLabel).build())
			.addTag(workName)
			.build()
		workRepository.add(workRequest.id)
		workManager.enqueue(workRequest).await()
		return Work(workRequest.id, workLabel)
	}

	override fun getPendingWorkFlow() = workManager
		.getWorkInfosByTagLiveData(workName)
		.asFlow()
		.mapNotNull { workInfoList ->
			workInfoList.firstOrNull { workRepository.getIsPending(it.id) }
		}
		.onEach { workInfo ->
			if (workInfo.state == WorkInfo.State.CANCELLED) {
				workRepository.updateIsPending(workInfo.id, isPending = false)
			}
		}
		.distinctUntilChangedBy { it.id }
		.map { it.toWork(workLabel) }
}

fun workNotificationIntent(applicationContext: Context, workerParameters: WorkerParameters): PendingIntent {
	val workLabel = workerParameters.inputData.getSerializable<LocalizedString>(WORK_LABEL_KEY)
		?: return defaultNotificationIntent(applicationContext)
	return NavDeepLinkBuilder(applicationContext)
		.setGraph(R.navigation.okkei_nav_graph)
		.setDestination(R.id.work_fragment, WorkFragmentArgs(Work(workerParameters.id, workLabel)).toBundle())
		.createPendingIntent()
}

private fun defaultNotificationIntent(applicationContext: Context): PendingIntent {
	val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
	val flagImmutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
	return PendingIntent.getActivity(
		applicationContext, 0, launchIntent,
		PendingIntent.FLAG_UPDATE_CURRENT or flagImmutable
	)
}