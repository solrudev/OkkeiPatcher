package solru.okkeipatcher.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProgressData(
	val progress: Int = 0,
	val max: Int = 100,
	val isIndeterminate: Boolean = false
) : Parcelable