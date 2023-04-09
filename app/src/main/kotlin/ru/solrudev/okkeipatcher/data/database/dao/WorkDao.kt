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

package ru.solrudev.okkeipatcher.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import ru.solrudev.okkeipatcher.data.database.model.WorkModel
import java.util.*

@Dao
interface WorkDao : GenericDao<WorkModel> {

	@Query("UPDATE works SET is_pending = :isPending WHERE work_id = :id")
	suspend fun updateIsPendingByWorkId(id: UUID, isPending: Boolean)

	@Query("SELECT is_pending FROM works WHERE work_id = :id LIMIT 1")
	suspend fun getIsPendingByWorkId(id: UUID): Boolean?
}