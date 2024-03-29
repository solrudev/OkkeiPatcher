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

import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.supervisorScope
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.database.dao.WorkDao
import ru.solrudev.okkeipatcher.data.database.model.WorkModel
import ru.solrudev.okkeipatcher.data.worker.util.toWorkState
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkRepositoryImpl @Inject constructor(
	private val workDao: WorkDao,
	private val workManager: WorkManager
) : WorkRepository {

	override suspend fun add(workId: UUID) {
		val workModel = WorkModel(workId = workId)
		workDao.insert(workModel)
	}

	override suspend fun updateIsPending(workId: UUID, isPending: Boolean) {
		workDao.updateIsPendingByWorkId(workId, isPending)
	}

	override suspend fun getIsPending(workId: UUID): Boolean {
		return workDao.getIsPendingByWorkId(workId) ?: false
	}

	override fun cancelWork(workId: UUID) {
		workManager.cancelWorkById(workId)
	}

	override fun getWorkStateFlow(workId: UUID) = flow {
		supervisorScope {
			workManager
				.getWorkInfoByIdFlow(workId)
				.map(WorkInfo::toWorkState)
				.onEach(::emit)
				.filter { it.isFinished }
				.onEach { this@supervisorScope.cancel() }
				.collect()
		}
	}
}