package solru.okkeipatcher.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProgressData(val progress: Int = 0, val max: Int = 100) : Parcelable