package solru.okkeipatcher.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import solru.okkeipatcher.data.db.entity.WorkEntity
import java.util.*

@Dao
interface WorkDao : BaseDao<WorkEntity> {

	@Query("UPDATE works SET is_pending = :isPending WHERE id = :id")
	suspend fun updateIsPending(id: Int, isPending: Boolean)

	@Query("SELECT * FROM works WHERE work_id = :id LIMIT 1")
	suspend fun getByWorkId(id: UUID): WorkEntity?
}