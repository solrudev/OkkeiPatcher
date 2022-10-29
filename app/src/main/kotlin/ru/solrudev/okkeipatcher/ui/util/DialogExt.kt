package ru.solrudev.okkeipatcher.ui.util

import android.app.Dialog
import android.content.DialogInterface.OnDismissListener
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Displays the dialog. When [dismissEvent] or [ON_DESTROY] occurs in the provided lifecycle, dialog is dismissed.
 * Previously set [OnDismissListener] won't be invoked.
 * @param lifecycle a [Lifecycle] to be observed.
 * @param dismissEvent [Lifecycle.Event] on which dialog will be dismissed.
 */
fun Dialog.showWithLifecycle(lifecycle: Lifecycle, dismissEvent: Lifecycle.Event) {
	val dialogHolder = LifecycleAwareDialogHolder(this, dismissEvent)
	lifecycle.addObserver(dialogHolder)
	show()
}

/**
 * Creates an [AlertDialog] and displays it. When [dismissEvent] or [ON_DESTROY] occurs in the provided lifecycle,
 * dialog is dismissed.
 * Previously set [OnDismissListener] won't be invoked.
 * @param lifecycle a [Lifecycle] to be observed.
 * @param dismissEvent [Lifecycle.Event] on which dialog will be dismissed.
 */
fun AlertDialog.Builder.showWithLifecycle(lifecycle: Lifecycle, dismissEvent: Lifecycle.Event) {
	create().showWithLifecycle(lifecycle, dismissEvent)
}

/**
 * Lifecycle-aware wrapper for a dialog. When [dismissEvent] or [ON_DESTROY] occurs, dialog is dismissed.
 * Previously set [OnDismissListener] won't be invoked.
 * @param dialog a [Dialog] which should be dismissed on [dismissEvent].
 * @param dismissEvent [Lifecycle.Event] on which dialog will be dismissed.
 */
private class LifecycleAwareDialogHolder(
	private var dialog: Dialog?,
	private val dismissEvent: Lifecycle.Event
) : LifecycleEventObserver {

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
		if (event == dismissEvent || event == ON_DESTROY) {
			dialog?.dismissWithoutSideEffects()
			dialog = null
			source.lifecycle.removeObserver(this)
		}
	}
}

/**
 * Removes previously set [OnDismissListener] and dismisses the dialog.
 */
private fun Dialog.dismissWithoutSideEffects() {
	setOnDismissListener(null)
	dismiss()
}