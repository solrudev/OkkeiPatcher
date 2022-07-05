package ru.solrudev.okkeipatcher.data.util

import java.io.File

fun File.recreate() {
	delete()
	parentFile?.mkdirs()
	createNewFile()
}