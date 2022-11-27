package ru.solrudev.okkeipatcher.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.solrudev.okkeipatcher.data.database.converter.UuidConverter
import ru.solrudev.okkeipatcher.data.database.dao.WorkDao
import ru.solrudev.okkeipatcher.data.database.model.WorkModel

@Database(entities = [WorkModel::class], version = 1)
@TypeConverters(UuidConverter::class)
abstract class WorkDatabase : RoomDatabase() {
	abstract fun workDao(): WorkDao
}