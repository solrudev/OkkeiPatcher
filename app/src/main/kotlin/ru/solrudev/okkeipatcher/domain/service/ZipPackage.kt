package ru.solrudev.okkeipatcher.domain.service

import okio.Path

interface ZipPackage : AutoCloseable {
	suspend fun addFiles(files: List<Path>, root: String)
	suspend fun removeFiles(files: List<String>)
	suspend fun sign()
	suspend fun removeSignature()
}