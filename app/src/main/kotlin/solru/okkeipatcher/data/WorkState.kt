package solru.okkeipatcher.data

sealed class WorkState {
	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkState()
	data class Failed(val throwable: Throwable?) : WorkState()
	object Succeeded : WorkState()
	object Canceled : WorkState()
	object Unknown : WorkState()
}