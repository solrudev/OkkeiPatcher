package ru.solrudev.okkeipatcher.domain.service

import java.io.OutputStream

interface HttpDownloader {

	val progressMax: Int

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return Output hash. Empty string if [hashing] is `false`.
	 */
	suspend fun download(
		url: String,
		outputStream: OutputStream,
		hashing: Boolean = false,
		onProgressDeltaChanged: suspend (Int) -> Unit = {}
	): String
}