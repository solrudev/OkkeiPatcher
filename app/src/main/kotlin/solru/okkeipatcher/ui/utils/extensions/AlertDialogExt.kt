package solru.okkeipatcher.ui.utils.extensions

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import solru.okkeipatcher.ui.utils.LifecycleAwareDialogHolder

/**
 * Creates an [AlertDialog] and displays it. When [ON_STOP] event occurs in the provided lifecycle, dialog is dismissed.
 */
fun AlertDialog.Builder.showWithLifecycle(lifecycle: Lifecycle) {
	val dialogHolder = LifecycleAwareDialogHolder(create())
	lifecycle.addObserver(dialogHolder)
	dialogHolder.show()
}

/**
 * Displays the dialog. When [ON_STOP] event occurs in the provided lifecycle, dialog is dismissed.
 */
fun AlertDialog.showWithLifecycle(lifecycle: Lifecycle) {
	val dialogHolder = LifecycleAwareDialogHolder(this)
	lifecycle.addObserver(dialogHolder)
	dialogHolder.show()
}