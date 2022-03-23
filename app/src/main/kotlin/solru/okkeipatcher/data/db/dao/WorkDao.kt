package solru.okkeipatcher.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import solru.okkeipatcher.data.db.entity.WorkEntity
import java.util.*

@Dao
interface WorkDao : BaseDao<WorkEntity> {

	@Query("SELECT * FROM works WHERE work_id = :id LIMIT 1")
	suspend fun getByWorkId(id: UUID): WorkEntity?
}