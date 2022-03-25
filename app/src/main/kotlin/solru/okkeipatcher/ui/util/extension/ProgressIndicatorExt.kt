package solru.okkeipatcher.ui.util.extension

import androidx.core.view.isVisible
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.BaseProgressIndicatorSpec

inline var <T : BaseProgressIndicator<S>, S : BaseProgressIndicatorSpec> T.safeIsIndeterminate: Boolean
	get() = isIndeterminate
	set(value) {
		if (value && !isIndeterminate) {
			isVisible = false
		}
		isIndeterminate = value
		if (value) {
			isVisible = true
		}
	}