package solru.okkeipatcher.viewmodels

import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.core.*
import solru.okkeipatcher.core.base.PatchInfoStrategy
import solru.okkeipatcher.core.workers.PatchWorker
import solru.okkeipatcher.model.dto.Message
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.utils.DebugUtil
import solru.okkeipatcher.utils.Preferences
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class MainViewModel @Inject constructor(
	private val manifestRepository: ManifestRepository,
	private val appUpdateRepository: AppUpdateRepository,
	private val patchServiceProvider: Provider<PatchService>,
	private val restoreServiceProvider: Provider<RestoreService>,
	private val patchInfoStrategyProvider: Provider<PatchInfoStrategy>,
	private val debugUtil: DebugUtil
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

	private fun onProcessSaveDataEnabledChanged() {
		Preferences.set(AppKey.process_save_data_enabled.name, isProcessSaveDataEnabled)
	}

	private fun isPatched() = Preferences.get(AppKey.is_patched.name, false)

	var isRunning = false

	override fun onResume(owner: LifecycleOwner) {
		WorkManager.getInstance(MainApplication.context)
			.getWorkInfosForUniqueWork(PatchWorker.WORK_NAME)
			.get()
			.firstOrNull()
			.let {
				if (it == null) return
				collectScope.collectProgress(patchServiceProvider.get().progress)
				collectScope.collectStatus(patchServiceProvider.get().status)
				collectScope.collectMessages(patchServiceProvider.get().message)
				if (it.state.isFinished) {
					isRunning = false
					_patchText.value = R.string.patch
					return
				}
				isRunning = true
				_patchText.value = R.string.abort
				collectScope.collectWorkInfo(PatchWorker.WORK_NAME)
			}
	}

	fun patch() {
		isRunning = true
		_patchText.value = R.string.abort
		collectScope.collectProgress(patchServiceProvider.get().progress)
		collectScope.collectStatus(patchServiceProvider.get().status)
		collectScope.collectMessages(patchServiceProvider.get().message)
		viewModelScope.launch {
			try {
				val patchWorkRequest = OneTimeWorkRequest.from(PatchWorker::class.java)
				WorkManager.getInstance(MainApplication.context).enqueueUniqueWork(
					PatchWorker.WORK_NAME,
					ExistingWorkPolicy.KEEP,
					patchWorkRequest
				)
				collectWorkInfo(PatchWorker.WORK_NAME)
			} catch (e: Throwable) {
				if (e !is CancellationException) {
					debugUtil.writeBugReport(e)
					Log.e(this@MainViewModel::class.qualifiedName, e.message, e)
				}
			}
		}
	}

	private fun CoroutineScope.collectProgress(flow: Flow<ProgressData>) = launch {
		flow.collect {
			_progress.value = it.progress
			_progressMax.value = it.max
			_progressIndeterminate.value = it.isIndeterminate
		}
	}

	private fun CoroutineScope.collectStatus(flow: Flow<Int>) = launch {
		flow.collect {
			_status.value = it
		}
	}

	private fun CoroutineScope.collectMessages(flow: Flow<Message>) = launch {
		flow.collect {
			Log.i(
				this@MainViewModel::class.qualifiedName,
				MainApplication.context.getString(it.messageId)
			)
		}
	}

	private fun CoroutineScope.collectWorkInfo(workName: String): Job {
		val liveData = WorkManager.getInstance(MainApplication.context)
			.getWorkInfosForUniqueWorkLiveData(workName)
			.asFlow()
		return launch {
			liveData.collect {
				it.firstOrNull()?.let { workInfo ->
					if (workInfo.state.isFinished) {
						isRunning = false
						_patchText.value = R.string.patch
						_restoreText.value = R.string.restore
						_progress.value = 0
						_progressIndeterminate.value = false
						WorkManager.getInstance(MainApplication.context).pruneWork()
						collectScope.coroutineContext[Job]?.cancelChildren()
					}
				}
			}
		}
	}

	fun cancel() {
		WorkManager.getInstance(MainApplication.context).cancelAllWork()
		isRunning = false
		_patchText.value = R.string.patch
		_restoreText.value = R.string.restore
		_status.value = R.string.status_aborted
		_progress.value = 0
		_progressIndeterminate.value = false
		collectScope.coroutineContext[Job]?.cancelChildren()
	}

	override fun onStop(owner: LifecycleOwner) {
		collectScope.coroutineContext[Job]?.cancelChildren()
	}

	override fun onCleared() {
		collectScope.cancel()
	}
}