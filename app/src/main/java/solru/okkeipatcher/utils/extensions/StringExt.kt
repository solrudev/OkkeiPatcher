package solru.okkeipatcher.utils.extensions

inline val String.Companion.empty: String
	get() = ""

fun String.trimIndents() = buildString {
	this@trimIndents.lines().forEach {
		append("${it.trimStart()}\n")
	}
}.removeSuffix("\n")

fun String.isEmptyOrBlank() = this.isEmpty() || this.isBlank()
fun String.isNotEmptyOrBlank() = this.isNotEmpty() && this.isNotBlank()
