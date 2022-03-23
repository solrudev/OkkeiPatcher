package solru.okkeipatcher.data.db

import androidx.room.TypeConverter
import java.util.*

class UuidConverter {

	@TypeConverter
	fun uuidToString(uuid: UUID) = uuid.toString()

	@TypeConverter
	fun stringToUuid(string: String): UUID = UUID.fromString(string)
}