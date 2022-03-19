package solru.okkeipatcher.ui.state

data class HomeUiState(
	val isPatchEnabled: Boolean = true,
	val isRestoreEnabled: Boolean = false,
	val patchUpdatesAvailable: Boolean = false,
	val checkedForPatchUpdates: Boolean = false
)