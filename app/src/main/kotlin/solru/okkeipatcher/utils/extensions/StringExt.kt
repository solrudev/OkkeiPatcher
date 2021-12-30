package solru.okkeipatcher.utils.extensions

inline val String.Companion.empty: String
	get() = ""

fun String.isEmptyOrBlank() = this.isEmpty() || this.isBlank()