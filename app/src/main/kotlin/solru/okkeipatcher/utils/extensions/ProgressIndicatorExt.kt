package solru.okkeipatcher.utils.extensions

import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.BaseProgressIndicatorSpec

inline var <T : BaseProgressIndicator<S>, S : BaseProgressIndicatorSpec> T.indeterminate: Boolean
	get() = isIndeterminate
	set(value) {
		if (value) {
			hide()
		}
		isIndeterminate = value
		if (value) {
			show()
		}
	}