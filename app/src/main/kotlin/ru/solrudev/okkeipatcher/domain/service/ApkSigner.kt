package ru.solrudev.okkeipatcher.domain.service

import java.io.File

interface ApkSigner {
	suspend fun sign(apk: File)
	suspend fun removeSignature(apk: File)
}