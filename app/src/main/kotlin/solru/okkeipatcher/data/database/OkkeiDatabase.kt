package solru.okkeipatcher.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import solru.okkeipatcher.data.database.converter.UuidConverter
import solru.okkeipatcher.data.database.dao.WorkDao
import solru.okkeipatcher.data.database.model.WorkModel

@Database(entities = [WorkModel::class], version = 1)
@TypeConverters(UuidConverter::class)
abstract class OkkeiDatabase : RoomDatabase() {
	abstract fun workDao(): WorkDao
}