package ru.solrudev.okkeipatcher.ui.util.extension

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import ru.solrudev.okkeipatcher.ui.util.LifecycleAwareDialogHolder

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