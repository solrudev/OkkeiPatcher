package ru.solrudev.okkeipatcher.data.util

import android.net.Uri
import android.provider.DocumentsContract

private const val ANDROID_DATA = "primary:Android/data"
private const val EXTERNAL_STORAGE_PROVIDER_AUTHORITY = "com.android.externalstorage.documents"

val ANDROID_DATA_URI: Uri = DocumentsContract.buildDocumentUri(
	EXTERNAL_STORAGE_PROVIDER_AUTHORITY, ANDROID_DATA
)

val ANDROID_DATA_TREE_URI: Uri = DocumentsContract.buildTreeDocumentUri(
	EXTERNAL_STORAGE_PROVIDER_AUTHORITY, ANDROID_DATA
)