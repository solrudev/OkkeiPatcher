package ru.solrudev.okkeipatcher.ui.screen.work

import android.app.Dialog
import android.app.NotificationManager
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.navigation.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.ActivityWorkBinding
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.renderBy
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.*
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import ru.solrudev.okkeipatcher.ui.screen.work.model.percentDone
import ru.solrudev.okkeipatcher.ui.util.*

@AndroidEntryPoint
class WorkActivity : AppCompatActivity(R.layout.activity_work), FeatureView<WorkUiState> {

	private val viewModel by viewModels<WorkViewModel>()
	private val args by navArgs<WorkActivityArgs>()
	private val binding by viewBinding(ActivityWorkBinding::bind, R.id.container_work)
	private var currentCancelDialog: Dialog? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		onBackPressed {
			finishAffinity()
		}
		setupNavigation()
		viewModel.renderBy(this)
		binding.toolbarWork.title = args.work.label.resolve(this)
	}

	override fun onStart() {
		super.onStart()
		viewModel.dispatchEvent(StartObservingWork(args.work))
	}

	override fun onStop() {
		super.onStop()
		currentCancelDialog = null
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: WorkUiState) {
		binding.textviewWorkStatus.localizedText = uiState.status
		binding.textviewWorkPercentDone.text = getString(R.string.percent_done, uiState.percentDone)
		setProgress(uiState.progressData)
		if (uiState.isWorkSuccessful) {
			onWorkSucceeded(playAnimations = !uiState.animationsPlayed)
		}
		if (uiState.isWorkCanceled) {
			onWorkCanceled()
		}
		if (uiState.cancelWorkMessage.shouldShow) {
			showCancelWorkMessage(uiState.cancelWorkMessage.data)
		}
		if (uiState.errorMessage.shouldShow) {
			onError(uiState.errorMessage.data)
		}
	}

	private fun setupNavigation() {
		binding.buttonWork.setOnClickListener {
			viewModel.dispatchEvent(CancelRequested)
		}
	}

	private fun clearNotifications() {
		val notificationManager = getSystemService<NotificationManager>()
		notificationManager?.cancelAll()
	}

	private fun onWorkSucceeded(playAnimations: Boolean) {
		if (playAnimations) {
			startSuccessAnimations()
		}
		binding.buttonWork.setOnClickListener {
			finish()
		}
		binding.buttonWork.setText(R.string.button_text_ok)
		currentCancelDialog?.dismiss()
		clearNotifications()
	}

	private fun onWorkCanceled() {
		finish()
	}

	private fun onError(error: Message) {
		binding.buttonWork.isEnabled = false
		showErrorMessage(error)
		clearNotifications()
	}

	private fun startSuccessAnimations() {
		binding.buttonWork.alpha = 0f
		binding.buttonWork
			.animate()
			.alpha(1f)
			.setDuration(500)
			.setInterpolator(DecelerateInterpolator())
			.start()
		viewModel.dispatchEvent(AnimationsPlayed)
	}

	private fun setProgress(progressData: ProgressData) {
		binding.progressbarWork.max = progressData.max
		binding.progressbarWork.setProgressCompat(progressData.progress, true)
	}

	private fun showCancelWorkMessage(cancelWorkMessage: Message) {
		val dialog = createDialogBuilder(cancelWorkMessage)
			.setPositiveButton(R.string.button_text_abort) { _, _ ->
				viewModel.dispatchEvent(CancelWork(args.work))
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(CancelMessageDismissed)
				currentCancelDialog = null
			}
			.create()
		currentCancelDialog = dialog
		dialog.showWithLifecycle(lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(CancelMessageShown)
	}

	private fun showErrorMessage(errorMessage: Message) {
		val message = errorMessage.text.resolve(this)
		createDialogBuilder(errorMessage)
			.setNeutralButton(R.string.button_text_copy_to_clipboard) { _, _ ->
				copyTextToClipboard("Okkei Patcher Exception", message)
			}
			.setOnDismissListener {
				viewModel.dispatchEvent(ErrorDismissed)
				finish()
			}
			.showWithLifecycle(lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(ErrorShown)
	}
}