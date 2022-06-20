package ru.solrudev.okkeipatcher.io.util.extension

import java.io.File

fun File.recreate() {
	delete()
	parentFile?.mkdirs()
	createNewFile()
}