package solru.okkeipatcher.viewmodels

import android.os.Build
import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.core.*
import solru.okkeipatcher.core.base.PatchInfoStrategy
import solru.okkeipatcher.core.workers.BaseWorker
import solru.okkeipatcher.core.workers.PatchWorker
import solru.okkeipatcher.core.workers.RestoreWorker
import solru.okkeipatcher.model.dto.Message
import solru.okkeipatcher.utils.Preferences
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class MainViewModel @Inject constructor(
	private val manifestRepository: ManifestRepository,
	private val appUpdateRepository: AppUpdateRepository,
	private val patchInfoStrategyProvider: Provider<PatchInfoStrategy>
) : ViewModel(), DefaultLifecycleObserver {

	private val _patchText = MutableLiveData(R.string.patch)
	private val _restoreText = MutableLiveData(R.string.restore)
	private val _status = MutableLiveData(R.string.empty)
	private val _progress = MutableLiveData(0)
	private val _progressMax = MutableLiveData(100)
	private val _progressIndeterminate = MutableLiveData(false)
	private val _isPatchEnabled = MutableLiveData(!isPatched())
	private val _isRestoreEnabled = MutableLiveData(isPatched())
	private val _isClearDataEnabled = MutableLiveData(true)
	private val _errorMessage = MutableLiveData<Message>()

	val patchText: LiveData<Int> get() = _patchText
	val restoreText: LiveData<Int> get() = _restoreText
	val status: LiveData<Int> get() = _status
	val progress: LiveData<Int> get() = _progress
	val progressMax: LiveData<Int> get() = _progressMax
	val progressIndeterminate: LiveData<Boolean> get() = _progressIndeterminate
	val isPatchEnabled: LiveData<Boolean> get() = _isPatchEnabled
	val isRestoreEnabled: LiveData<Boolean> get() = _isRestoreEnabled
	val isClearDataEnabled: LiveData<Boolean> get() = _isClearDataEnabled
	val errorMessage: LiveData<Message> get() = _errorMessage

	var isProcessSaveDataEnabled = Preferences.get(
		AppKey.process_save_data_enabled.name,
		Build.VERSION.SDK_INT < Build.VERSION_CODES.R
	)
		set(value) {
			if (field == value) return
			field = value
			onProcessSaveDataEnabledChanged()
		}

	private val collectScope = CoroutineScope(Dispatchers.Main.immediate)

	var isRunning = false
		private set

	fun patch() = startUniqueWork(PatchWorker::class.java, PatchWorker.WORK_NAME)
	fun restore() = startUniqueWork(RestoreWorker::class.java, RestoreWorker.WORK_NAME)
	fun cancel() = WorkManager.getInstance(MainApplication.context).cancelAllWork()

	private fun startUniqueWork(workerClass: Class<out ListenableWorker>, workName: String) {
		viewModelScope.launch {
			val workRequest = OneTimeWorkRequest.from(workerClass)
			WorkManager.getInstance(MainApplication.context).enqueueUniqueWork(
				workName,
				ExistingWorkPolicy.KEEP,
				workRequest
			)
			collectWorkInfo(workName)
		}
	}

	private fun CoroutineScope.collectWorkInfo(workName: String): Job {
		val workInfoFlow = WorkManager.getInstance(MainApplication.context)
			.getWorkInfosForUniqueWorkLiveData(workName)
			.asFlow()
		return launch {
			workInfoFlow.collect {
				it.firstOrNull()?.let { workInfo ->
					if (workInfo.state.isFinished) {
						workInfo.outputData
						isRunning = false
						_patchText.value = R.string.patch
						_restoreText.value = R.string.restore
						_isPatchEnabled.value = !isPatched()
						_isRestoreEnabled.value = isPatched()
						_isClearDataEnabled.value = true
						_progress.value = 0
						_progressMax.value = 100
						_progressIndeterminate.value = false
						when (workInfo.state) {
							WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> _status.value =
								R.string.status_aborted
							WorkInfo.State.SUCCEEDED -> when (workName) {
								PatchWorker.WORK_NAME -> R.string.status_patch_success
								RestoreWorker.WORK_NAME -> R.string.status_restore_success
							}
						}
						WorkManager.getInstance(MainApplication.context).pruneWork()
						collectScope.coroutineContext[Job]?.cancelChildren()
						return@let
					}
					isRunning = true
					when (workName) {
						PatchWorker.WORK_NAME -> _patchText.value = R.string.abort
						RestoreWorker.WORK_NAME -> _restoreText.value = R.string.abort
					}
					_isClearDataEnabled.value = false
					with(workInfo.progress) {
						_progress.value = getInt(BaseWorker.KEY_PROGRESS, 0)
						_progressMax.value = getInt(BaseWorker.KEY_PROGRESS_MAX, 100)
						_progressIndeterminate.value =
							getBoolean(BaseWorker.KEY_PROGRESS_INDETERMINATE, false)
						_status.value = getInt(BaseWorker.KEY_STATUS, R.string.empty)
					}
				}
			}
		}
	}

	private fun observeRunningWorker(workName: String) {
		WorkManager.getInstance(MainApplication.context)
			.getWorkInfosForUniqueWork(workName)
			.get()
			.firstOrNull()
			?.let { collectScope.collectWorkInfo(workName) }
	}

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)

	private fun onProcessSaveDataEnabledChanged() {
		Preferences.set(AppKey.process_save_data_enabled.name, isProcessSaveDataEnabled)
	}

	override fun onResume(owner: LifecycleOwner) {
		observeRunningWorker(PatchWorker.WORK_NAME)
		observeRunningWorker(RestoreWorker.WORK_NAME)
	}

	override fun onStop(owner: LifecycleOwner) {
		collectScope.coroutineContext[Job]?.cancelChildren()
	}

	override fun onCleared() {
		collectScope.cancel()
	}
}