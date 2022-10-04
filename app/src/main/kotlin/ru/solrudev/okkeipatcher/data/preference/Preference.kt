package ru.solrudev.okkeipatcher.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.domain.core.persistence.ReactiveDao

open class MappedPreference<DomainType, DataType>(
	private val key: Preferences.Key<DataType>,
	private val toDataType: (DomainType) -> DataType,
	private val toDomainType: (DataType?) -> DomainType,
	private val preferences: DataStore<Preferences>
) : ReactiveDao<DomainType> {

	override val flow = preferences.data.map { toDomainType(it[key]) }

	override suspend fun retrieve() = flow.first()

	override suspend fun persist(value: DomainType) {
		preferences.edit {
			it[key] = toDataType(value)
		}
	}

	override suspend fun clear() {
		preferences.edit {
			it.remove(key)
		}
	}
}

class Preference<T>(
	key: Preferences.Key<T>,
	defaultValue: () -> T,
	preferences: DataStore<Preferences>
) : MappedPreference<T, T>(
	key,
	toDataType = { it },
	toDomainType = { it ?: defaultValue() },
	preferences
)