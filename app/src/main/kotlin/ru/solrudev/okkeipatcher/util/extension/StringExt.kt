package ru.solrudev.okkeipatcher.util.extension

inline val String.Companion.empty: String
	get() = ""

fun String.isEmptyOrBlank() = this.isEmpty() || this.isBlank()