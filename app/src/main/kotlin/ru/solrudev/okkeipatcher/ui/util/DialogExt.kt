package ru.solrudev.okkeipatcher.ui.util

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Displays the dialog. When [dismissEvent] occurs in the provided lifecycle, dialog is dismissed.
 * @param lifecycle a [Lifecycle] to be observed.
 * @param dismissEvent [Lifecycle.Event] on which dialog will be dismissed.
 */
fun Dialog.showWithLifecycle(lifecycle: Lifecycle, dismissEvent: Lifecycle.Event) {
	val dialogHolder = LifecycleAwareDialogHolder(this, dismissEvent)
	lifecycle.addObserver(dialogHolder)
	show()
}

/**
 * Creates an [AlertDialog] and displays it. When [dismissEvent] occurs in the provided lifecycle, dialog is dismissed.
 * @param lifecycle a [Lifecycle] to be observed.
 * @param dismissEvent [Lifecycle.Event] on which dialog will be dismissed.
 */
fun AlertDialog.Builder.showWithLifecycle(lifecycle: Lifecycle, dismissEvent: Lifecycle.Event) {
	create().showWithLifecycle(lifecycle, dismissEvent)
}

/**
 * Lifecycle-aware wrapper for a dialog. When [dismissEvent] occurs, dialog is dismissed.
 * @param dialog a [Dialog] which should be dismissed on [dismissEvent].
 * @param dismissEvent [Lifecycle.Event] on which dialog will be dismissed.
 */
private class LifecycleAwareDialogHolder(
	private var dialog: Dialog?,
	private val dismissEvent: Lifecycle.Event
) : LifecycleEventObserver {

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
		if (event == dismissEvent) {
			dialog?.setOnDismissListener(null)
			dialog?.dismiss()
			dialog = null
		}
		if (event == Lifecycle.Event.ON_DESTROY) {
			source.lifecycle.removeObserver(this)
		}
	}
}