package ru.solrudev.okkeipatcher.domain.worker

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.AppKey
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.util.Preferences
import kotlin.time.Duration.Companion.seconds

class MockWorker(context: Context, workerParameters: WorkerParameters) : ForegroundWorker(context, workerParameters) {

	override val progressNotificationTitle: LocalizedString
		get() = LocalizedString.resource(R.string.notification_title_test)

	override suspend fun getOperation() = object : AbstractOperation<Unit>() {

		private val stepsCount = 5
		override val progressMax = stepsCount * 100

		override suspend fun invoke() {
			repeat(stepsCount) { stepIndex ->
				val index = stepIndex + 1
				_status.emit(LocalizedString.raw(index.toString().repeat(10)))
				delay(1.seconds)
				_progressDelta.emit(100)
			}
			Preferences.set(AppKey.is_patched.name, tags.contains("PatchWork"))
		}
	}

	override fun createPendingIntent(): PendingIntent {
		val navDeepLinkBuilder = NavDeepLinkBuilder(applicationContext)
			.setGraph(R.navigation.okkei_nav_graph)
		if (tags.contains("PatchWork")) {
			return navDeepLinkBuilder
				.setDestination(R.id.patch_fragment)
				.createPendingIntent()
		}
		if (tags.contains("RestoreWork")) {
			return navDeepLinkBuilder
				.setDestination(R.id.restore_fragment)
				.createPendingIntent()
		}
		return navDeepLinkBuilder
			.setDestination(R.id.home_fragment)
			.createPendingIntent()
	}
}