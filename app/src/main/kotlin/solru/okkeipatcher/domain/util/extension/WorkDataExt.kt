package solru.okkeipatcher.util.extension

import android.os.Parcel
import android.os.Parcelable
import androidx.work.Data
import java.io.*

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Parcelable> Data.getParcelable(key: String): T? {
	val bytes = getByteArray(key) ?: return null
	val parcel = Parcel.obtain()
	try {
		parcel.unmarshall(bytes, 0, bytes.size)
		parcel.setDataPosition(0)
		val creator = T::class.java.getField("CREATOR").get(null) as Parcelable.Creator<T>
		return creator.createFromParcel(parcel)
	} finally {
		parcel.recycle()
	}
}

fun Data.Builder.putParcelable(key: String, parcelable: Parcelable): Data.Builder {
	val parcel = Parcel.obtain()
	try {
		parcelable.writeToParcel(parcel, 0)
		putByteArray(key, parcel.marshall())
	} finally {
		parcel.recycle()
	}
	return this
}

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Data.getSerializable(key: String): T? {
	val byteArray = getByteArray(key) ?: return null
	ByteArrayInputStream(byteArray).use { byteArrayInputStream ->
		ObjectInputStream(byteArrayInputStream).use { objectInputStream ->
			return objectInputStream.readObject() as T
		}
	}
}

fun Data.Builder.putSerializable(key: String, value: Serializable): Data.Builder {
	ByteArrayOutputStream().use { byteArrayOutputStream ->
		ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
			objectOutputStream.writeObject(value)
			objectOutputStream.flush()
		}
		putByteArray(key, byteArrayOutputStream.toByteArray())
	}
	return this
}