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
	private val _isProgressIndeterminate = MutableLiveData(false)
	private val _isPatchEnabled = MutableLiveData(!isPatched())
	private val _isRestoreEnabled = MutableLiveData(isPatched())
	private val _isClearDataEnabled = MutableLiveData(true)
	private val _errorMessage = MutableLiveData<Message>()

	val patchText: LiveData<Int> get() = _patchText
	val restoreText: LiveData<Int> get() = _restoreText
	val status: LiveData<Int> get() = _status
	val progress: LiveData<Int> get() = _progress
	val progressMax: LiveData<Int> get() = _progressMax
	val isProgressIndeterminate: LiveData<Boolean> get() = _isProgressIndeterminate
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

	var isRunning = false
		private set

	private val workObservingScope = CoroutineScope(Dispatchers.Main.immediate)

	fun patch() = startUniqueWork<PatchWorker>(PatchWorker.WORK_NAME)
	fun restore() = startUniqueWork<RestoreWorker>(RestoreWorker.WORK_NAME)
	fun cancel() = WorkManager.getInstance(MainApplication.context).cancelAllWork()

	private inline fun <reified T : ListenableWorker> startUniqueWork(workName: String) {
		val workRequest = OneTimeWorkRequest.from(T::class.java)
		WorkManager.getInstance(MainApplication.context).enqueueUniqueWork(
			workName,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		workObservingScope.observeWork(workRequest.id)
	}

	@Suppress("NON_EXHAUSTIVE_WHEN")
	private fun CoroutineScope.observeWork(workId: UUID) = launch {
		var isWorkStarted = false
		WorkManager.getInstance(MainApplication.context)
			.getWorkInfoByIdLiveData(workId)
			.asFlow()
			.collect {
				if (it == null) return@collect
				with(it.progress) {
					_status.value = getInt(BaseWorker.KEY_STATUS, R.string.empty)
					_progress.value = getInt(BaseWorker.KEY_PROGRESS, 0)
					_progressMax.value = getInt(BaseWorker.KEY_PROGRESS_MAX, 100)
					_isProgressIndeterminate.value =
						getBoolean(BaseWorker.KEY_IS_PROGRESS_INDETERMINATE, false)
				}
				if (it.state.isFinished) {
					resetState()
					isWorkStarted = false
					when (it.state) {
						WorkInfo.State.FAILED, WorkInfo.State.CANCELLED ->
							_status.value = R.string.status_aborted
						WorkInfo.State.SUCCEEDED -> when (it.tags.firstOrNull()) {
							PatchWorker::class.java.name ->
								_status.value = R.string.status_patch_success
							RestoreWorker::class.java.name ->
								_status.value = R.string.status_restore_success
						}
					}
					WorkManager.getInstance(MainApplication.context).pruneWork()
					workObservingScope.coroutineContext[Job]?.cancelChildren()
					return@collect
				}
				if (!isRunning) {
					isRunning = true
				}
				if (!isWorkStarted) {
					isWorkStarted = true
					when (it.tags.firstOrNull()) {
						PatchWorker::class.java.name -> _patchText.value = R.string.abort
						RestoreWorker::class.java.name -> _restoreText.value = R.string.abort
					}
					_isClearDataEnabled.value = false
				}
			}
	}

	private fun resetState() {
		isRunning = false
		_patchText.value = R.string.patch
		_restoreText.value = R.string.restore
		_isPatchEnabled.value = !isPatched()
		_isRestoreEnabled.value = isPatched()
		_isClearDataEnabled.value = true
		_progress.value = 0
		_progressMax.value = 100
		_isProgressIndeterminate.value = false
	}

	private fun observeRunningWork(workName: String) =
		WorkManager.getInstance(MainApplication.context)
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