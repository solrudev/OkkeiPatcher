package solru.okkeipatcher.utils

import android.os.Build
import solru.okkeipatcher.utils.extensions.trimIndents
import javax.inject.Inject

class DebugUtil @Inject constructor() {

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
           """.trimIndents()
}