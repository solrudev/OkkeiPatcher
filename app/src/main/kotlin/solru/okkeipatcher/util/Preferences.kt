package solru.okkeipatcher.util

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import solru.okkeipatcher.OkkeiApplication

object Preferences {

	private val locker = Any()

	private val sharedPreferences: SharedPreferences
		get() = PreferenceManager.getDefaultSharedPreferences(OkkeiApplication.context)

	fun get(key: String, defaultValue: String) = sharedPreferences.getString(key, defaultValue)!!
	fun get(key: String, defaultValue: Int) = sharedPreferences.getInt(key, defaultValue)
	fun get(key: String, defaultValue: Boolean) = sharedPreferences.getBoolean(key, defaultValue)
	fun get(key: String, defaultValue: Long) = sharedPreferences.getLong(key, defaultValue)
	fun get(key: String, defaultValue: Float) = sharedPreferences.getFloat(key, defaultValue)

	fun set(key: String, value: Any) {
		synchronized(locker) {
			sharedPreferences.edit { put(key, value) }
		}
	}

	fun containsKey(key: String) = sharedPreferences.contains(key)

	fun remove(key: String) {
		synchronized(locker) {
			sharedPreferences.edit { remove(key) }
		}
	}

	fun clear() {
		synchronized(locker) {
			sharedPreferences.edit { clear() }
		}
	}

	private fun SharedPreferences.Editor.put(key: String, value: Any) {
		when (value) {
			is String -> putString(key, value)
			is Int -> putInt(key, value)
			is Boolean -> putBoolean(key, value)
			is Long -> putLong(key, value)
			is Float -> putFloat(key, value)
			else -> throw IllegalArgumentException("Only primitive types can be stored in SharedPreferences")
		}
	}
}