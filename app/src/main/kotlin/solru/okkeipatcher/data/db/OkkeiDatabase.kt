package solru.okkeipatcher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import solru.okkeipatcher.data.db.dao.WorkDao
import solru.okkeipatcher.data.db.entity.WorkEntity

@Database(entities = [WorkEntity::class], version = 1)
@TypeConverters(UuidConverter::class)
abstract class OkkeiDatabase : RoomDatabase() {
	abstract fun workDao(): WorkDao
}