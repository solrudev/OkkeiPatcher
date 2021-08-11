package solru.okkeipatcher.model.dto

data class ProgressData(
	val progress: Int = 0,
	val max: Int = 100,
	val isIndeterminate: Boolean = false
)