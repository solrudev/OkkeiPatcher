package solru.okkeipatcher.viewmodels

import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.core.*
import solru.okkeipatcher.core.strategy.PatchDataStrategy
import solru.okkeipatcher.core.workers.BaseWorker
import solru.okkeipatcher.core.workers.PatchWorker
import solru.okkeipatcher.core.workers.RestoreWorker
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.ProgressData
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.getSerializable
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class MainViewModel @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository,
	private val patchDataStrategyProvider: Provider<PatchDataStrategy>
) : ViewModel(), DefaultLifecycleObserver {

	private val _patchText = MutableStateFlow<LocalizedString>(LocalizedString.resource(R.string.patch))
	private val _restoreText = MutableStateFlow<LocalizedString>(LocalizedString.resource(R.string.restore))
	private val _status = MutableStateFlow<LocalizedString>(LocalizedString.empty())
	private val _progressData = MutableStateFlow(ProgressData())
	private val _isPatchEnabled = MutableStateFlow(!isPatched())
	private val _isRestoreEnabled = MutableStateFlow(isPatched())
	private val _isClearDataEnabled = MutableStateFlow(true)

	val patchText = _patchText.asStateFlow()
	val restoreText = _restoreText.asStateFlow()
	val status = _status.asStateFlow()
	val progressData = _progressData.asStateFlow()
	val isPatchEnabled = _isPatchEnabled.asStateFlow()
	val isRestoreEnabled = _isRestoreEnabled.asStateFlow()
	val isClearDataEnabled = _isClearDataEnabled.asStateFlow()

	var isProcessSaveDataEnabled = Preferences.get(
		AppKey.process_save_data_enabled.name,
		Build.VERSION.SDK_INT < Build.VERSION_CODES.R
	)
		set(value) {
			if (field == value) return
			field = value
			onProcessSaveDataEnabledChanged()
		}

	var isRunning = false
		private set

	private val workObservingScope = CoroutineScope(Dispatchers.Main.immediate)

	fun patch() = startUniqueWork<PatchWorker>(PatchWorker.WORK_NAME)
	fun restore() = startUniqueWork<RestoreWorker>(RestoreWorker.WORK_NAME)
	fun cancel() = WorkManager.getInstance(OkkeiApplication.context).cancelAllWork()

	private inline fun <reified T : ListenableWorker> startUniqueWork(workName: String) {
		val workRequest = OneTimeWorkRequest.from(T::class.java)
		WorkManager.getInstance(OkkeiApplication.context).enqueueUniqueWork(
			workName,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		workObservingScope.observeWork(workRequest.id)
	}

	private fun CoroutineScope.observeWork(workId: UUID) = launch {
		var isWorkStarted = false
		WorkManager.getInstance(OkkeiApplication.context)
			.getWorkInfoByIdLiveData(workId)
			.asFlow()
			.collect {
				if (it == null) return@collect
				with(it.progress) {
					_status.value = getSerializable(BaseWorker.KEY_STATUS) ?: LocalizedString.empty()
					_progressData.value = getSerializable(BaseWorker.KEY_PROGRESS_DATA) ?: ProgressData()
				}
				if (it.state.isFinished) {
					resetState()
					isWorkStarted = false
					_status.value = when (it.state) {
						WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> LocalizedString.resource(R.string.status_aborted)
						WorkInfo.State.SUCCEEDED -> when (it.tags.firstOrNull()) {
							PatchWorker::class.java.name -> LocalizedString.resource(R.string.status_patch_success)
							RestoreWorker::class.java.name -> LocalizedString.resource(R.string.status_restore_success)
							else -> LocalizedString.empty()
						}
						else -> LocalizedString.empty()
					}
					if (it.state == WorkInfo.State.FAILED) {
						val exception = it.outputData.getSerializable<Throwable>(BaseWorker.KEY_FAILURE_CAUSE)
						Log.e(this@MainViewModel::class.java.name, "", exception)
					}
					WorkManager.getInstance(OkkeiApplication.context).pruneWork()
					workObservingScope.coroutineContext[Job]?.cancelChildren()
					return@collect
				}
				if (!isRunning) {
					isRunning = true
				}
				if (!isWorkStarted) {
					isWorkStarted = true
					when (it.tags.firstOrNull()) {
						PatchWorker::class.java.name -> _patchText.value = LocalizedString.resource(R.string.abort)
						RestoreWorker::class.java.name -> _restoreText.value = LocalizedString.resource(R.string.abort)
					}
					_isClearDataEnabled.value = false
				}
			}
	}

	private fun resetState() {
		isRunning = false
		_patchText.value = LocalizedString.resource(R.string.patch)
		_restoreText.value = LocalizedString.resource(R.string.restore)
		_isPatchEnabled.value = !isPatched()
		_isRestoreEnabled.value = isPatched()
		_isClearDataEnabled.value = true
		_progressData.value = ProgressData()
	}

	private fun observeRunningWork(workName: String) =
		WorkManager.getInstance(OkkeiApplication.context)
			.getWorkInfosForUniqueWork(workName)
			.get()
			.firstOrNull()
			?.let { workObservingScope.observeWork(it.id) }

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)

	private fun onProcessSaveDataEnabledChanged() {
		Preferences.set(AppKey.process_save_data_enabled.name, isProcessSaveDataEnabled)
	}

	override fun onResume(owner: LifecycleOwner) {
		observeRunningWork(PatchWorker.WORK_NAME)
		observeRunningWork(RestoreWorker.WORK_NAME)
	}

	override fun onStop(owner: LifecycleOwner) {
		workObservingScope.coroutineContext[Job]?.cancelChildren()
	}

	override fun onCleared() {
		workObservingScope.cancel()
	}
}