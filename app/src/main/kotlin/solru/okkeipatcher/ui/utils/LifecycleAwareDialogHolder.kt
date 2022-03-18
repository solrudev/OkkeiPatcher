package solru.okkeipatcher.ui.utils

import android.app.Dialog
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleOwner

/**
 * Lifecycle-aware wrapper for a dialog. When [ON_STOP] event occurs, dialog is dismissed.
 */
class LifecycleAwareDialogHolder(private var dialog: Dialog?) : DefaultLifecycleObserver {

	override fun onStop(owner: LifecycleOwner) {
		dialog?.setOnDismissListener { }
		dialog?.dismiss()
		dialog = null
	}

	override fun onDestroy(owner: LifecycleOwner) {
		owner.lifecycle.removeObserver(this)
	}

	/**
	 * Displays the dialog.
	 */
	fun show() {
		dialog?.show()
	}
}