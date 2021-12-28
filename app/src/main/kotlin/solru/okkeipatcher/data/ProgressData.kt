package solru.okkeipatcher.data

import java.io.Serializable

data class ProgressData(
	val progress: Int = 0,
	val max: Int = 100,
	val isIndeterminate: Boolean = false
) : Serializable