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