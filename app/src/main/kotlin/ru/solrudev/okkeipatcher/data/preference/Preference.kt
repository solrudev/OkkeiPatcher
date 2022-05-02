package ru.solrudev.okkeipatcher.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.domain.persistence.Dao

open class MappedPreference<DomainType, DataType>(
	private val key: Preferences.Key<DataType>,
	private val toDataType: (DomainType) -> DataType,
	private val toDomainType: (DataType?) -> DomainType,
	private val preferences: DataStore<Preferences>
) : Dao<DomainType> {

	override suspend fun retrieve() = toDomainType(
		preferences.data
			.map { it[key] }
			.first()
	)

	override suspend fun persist(value: DomainType) {
		preferences.edit {
			it[key] = toDataType(value)
		}
	}
}

class Preference<T>(
	key: Preferences.Key<T>,
	defaultValue: T,
	preferences: DataStore<Preferences>
) : MappedPreference<T, T>(
	key,
	toDataType = { it },
	toDomainType = { it ?: defaultValue },
	preferences
)