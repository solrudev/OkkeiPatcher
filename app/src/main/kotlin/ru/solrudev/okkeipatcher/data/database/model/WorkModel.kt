package ru.solrudev.okkeipatcher.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "works")
data class WorkModel(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	@ColumnInfo(name = "work_id") val workId: UUID,
	@ColumnInfo(name = "is_pending") val isPending: Boolean = true
)