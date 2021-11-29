package solru.okkeipatcher.utils

import android.os.Build
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.utils.extensions.empty
import solru.okkeipatcher.utils.extensions.trimIndents
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DebugUtil @Inject constructor(private val ioService: IoService) {

	private val bugReportFile = File(OkkeiStorage.external, "bugreport.log")

	fun getSuppressedExceptionsStackTraces(e: Throwable) = buildString {
		e.suppressedExceptions.forEach {
			append("${it.stackTraceToString()}\n")
		}
	}.removeSuffix("\n")

	fun getSharedPreferencesValues() = buildString {
		Preferences.all.forEach {
			append("${it.key}: ${it.value}\n")
		}
	}.removeSuffix("\n")

	fun getDeviceInfo() =
		"""manufacturer:       ${Build.MANUFACTURER}
           model:              ${Build.MODEL}
           product:            ${Build.PRODUCT}
           incremental:        ${Build.VERSION.INCREMENTAL}
           release:            ${Build.VERSION.RELEASE}
           sdkInt:             ${Build.VERSION.SDK_INT}"""

	fun getBugReportText(e: Throwable): String =
		"""Okkei Patcher
           ----------------------------------
           Version code: $appVersionCode
           Version name: $appVersionString
           ----------------------------------
           
           Device info
           ----------------------------------
           ${getDeviceInfo()}
           ----------------------------------
           
           Shared preferences
           ----------------------------------
           ${getSharedPreferencesValues()}
           ----------------------------------
           
           Exception stack trace
           ----------------------------------
           ${e.stackTraceToString()}
           ----------------------------------
           ${
			if (e.suppressedExceptions.isNotEmpty())
				"""
                   Suppressed exceptions
                   ----------------------------------
                   ${getSuppressedExceptionsStackTraces(e)}
                   ----------------------------------""" else String.empty
		}"""
			.trimIndents()

	suspend fun writeBugReport(e: Throwable) {
		ioService.writeAllText(FileOutputStream(bugReportFile), getBugReportText(e))
	}
}