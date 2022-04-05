package ru.solrudev.okkeipatcher.ui.util

import android.app.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Lifecycle-aware wrapper for a dialog. When [dismissEvent] occurs, dialog is dismissed.
 * @param dialog a [Dialog] which should be dismissed on [dismissEvent].
 * @param dismissEvent [Lifecycle.Event] on which dialog will be dismissed.
 */
class LifecycleAwareDialogHolder(
	private var dialog: Dialog?,
	private val dismissEvent: Lifecycle.Event
) : LifecycleEventObserver {

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
		if (event == dismissEvent) {
			dialog?.setOnDismissListener { }
			dialog?.dismiss()
			dialog = null
		}
		if (event == Lifecycle.Event.ON_DESTROY) {
			source.lifecycle.removeObserver(this)
		}
	}
}