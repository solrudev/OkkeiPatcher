package ru.solrudev.okkeipatcher.data.worker.util.extension

import androidx.work.Data
import java.io.*

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Data.getSerializable(key: String): T? {
	val byteArray = getByteArray(key) ?: return null
	ByteArrayInputStream(byteArray).use { byteArrayInputStream ->
		ObjectInputStream(byteArrayInputStream).use { objectInputStream ->
			return objectInputStream.readObject() as T
		}
	}
}

fun Data.Builder.putSerializable(key: String, value: Serializable) =
	ByteArrayOutputStream().use { byteArrayOutputStream ->
		ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
			objectOutputStream.writeObject(value)
			objectOutputStream.flush()
		}
		putByteArray(key, byteArrayOutputStream.toByteArray())
	}