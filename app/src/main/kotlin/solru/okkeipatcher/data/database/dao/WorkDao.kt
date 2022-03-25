package solru.okkeipatcher.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import solru.okkeipatcher.data.database.model.WorkModel
import java.util.*

@Dao
interface WorkDao : GenericDao<WorkModel> {

	@Query("UPDATE works SET is_pending = :isPending WHERE work_id = :id")
	suspend fun updateIsPendingByWorkId(id: UUID, isPending: Boolean)

	@Query("SELECT is_pending FROM works WHERE work_id = :id LIMIT 1")
	suspend fun getIsPendingByWorkId(id: UUID): Boolean?
}