package ru.solrudev.okkeipatcher.domain.service.util

import java.io.File

fun File.recreate() {
	delete()
	parentFile?.mkdirs()
	createNewFile()
}