package ru.solrudev.okkeipatcher.domain.model

data class PatchFileData(
	val version: Int,
	val url: String,
	val hash: String,
	val size: Long
)