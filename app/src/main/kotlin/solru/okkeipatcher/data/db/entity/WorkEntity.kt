package solru.okkeipatcher.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "works")
data class WorkEntity(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	@ColumnInfo(name = "work_id") val workId: UUID,
	@ColumnInfo(name = "is_pending") val isPending: Boolean = true
)