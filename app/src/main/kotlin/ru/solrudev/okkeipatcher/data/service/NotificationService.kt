/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentCallbacks
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.content.res.Configuration
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.work.ForegroundInfo
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

private val globalProgressNotificationId = AtomicInteger(Random.nextInt(from = 10000, until = 1000000))
private val globalMessageNotificationId = AtomicInteger(Random.nextInt(from = 10000, until = 1000000))

interface NotificationService : AutoCloseable {
	fun createForegroundInfo(): ForegroundInfo
	fun updateProgressNotification(status: LocalizedString, progressData: ProgressData)
	fun displayMessageNotification(message: Message)
}

class NotificationServiceImpl(
	private val applicationContext: Context,
	private val progressNotificationTitle: LocalizedString,
	private val contentIntent: PendingIntent,
	private val cancelIntent: PendingIntent?,
	showGameIconInProgressNotification: Boolean
) : NotificationService {

	private val progressNotificationBuilder = createNotificationBuilder(
		progressNotificationTitle,
		progressNotification = true,
		showGameIconInProgressNotification
	)

	private val configChangeCallback = object : ComponentCallbacks {
		override fun onConfigurationChanged(newConfig: Configuration) = updateProgressNotificationStrings()
		override fun onLowMemory() {}
	}

	private val progressNotificationId = globalProgressNotificationId.incrementAndGet()
	private val notificationManager = applicationContext.getSystemService<NotificationManager>()
	private var currentStatus: LocalizedString = LocalizedString.empty()

	init {
		applicationContext.registerComponentCallbacks(configChangeCallback)
	}

	override fun createForegroundInfo(): ForegroundInfo {
		return if (Build.VERSION.SDK_INT >= 34) {
			ForegroundInfo(
				progressNotificationId,
				progressNotificationBuilder.build(),
				FOREGROUND_SERVICE_TYPE_SPECIAL_USE
			)
		} else {
			ForegroundInfo(progressNotificationId, progressNotificationBuilder.build())
		}
	}

	override fun updateProgressNotification(status: LocalizedString, progressData: ProgressData) {
		currentStatus = status
		val titleString = progressNotificationTitle.resolve(applicationContext)
		val statusString = status.resolve(applicationContext)
		val notification = progressNotificationBuilder
			.setContentTitle(titleString)
			.setContentText(statusString)
			.setSubText(
				applicationContext.getString(
					R.string.percent_done,
					(progressData.progress.toDouble() / progressData.max * 100).toInt()
				)
			)
			.setProgress(progressData.max, progressData.progress, false)
			.setStyle(NotificationCompat.BigTextStyle().bigText(statusString))
			.build()
		notificationManager?.notify(progressNotificationId, notification)
	}

	override fun displayMessageNotification(message: Message) {
		val messageString = message.text.resolve(applicationContext)
		val notification = createNotificationBuilder(
			message.title,
			progressNotification = false,
			showGameIcon = false
		).apply {
			setContentText(messageString)
			if (messageString.length > 40) {
				setStyle(NotificationCompat.BigTextStyle().bigText(messageString))
			}
		}.build()
		val notificationId = globalMessageNotificationId.incrementAndGet()
		notificationManager?.notify(notificationId, notification)
	}

	override fun close() {
		applicationContext.unregisterComponentCallbacks(configChangeCallback)
	}

	private fun createNotificationBuilder(
		title: LocalizedString,
		progressNotification: Boolean,
		showGameIcon: Boolean
	): NotificationCompat.Builder {
		val contentTitle = title.resolve(applicationContext)
		val channelId = if (progressNotification) {
			applicationContext.getString(R.string.notification_channel_progress_id)
		} else {
			applicationContext.getString(R.string.notification_channel_messages_id)
		}
		return NotificationCompat.Builder(applicationContext, channelId).apply {
			setContentTitle(contentTitle)
			setContentText("")
			priority = NotificationCompat.PRIORITY_DEFAULT
			setSmallIcon(R.drawable.ic_notification)
			setContentIntent(contentIntent)
			if (cancelIntent != null && progressNotification) {
				addAction(
					android.R.drawable.ic_delete,
					applicationContext.getString(R.string.button_text_abort),
					cancelIntent
				)
			}
			setSound(null)
			if (progressNotification) {
				setSubText(applicationContext.getString(R.string.percent_done, 0))
				if (showGameIcon) {
					runCatching { applicationContext.packageManager.getApplicationIcon(GAME_PACKAGE_NAME).toBitmap() }
						.getOrNull()?.let(::setLargeIcon)
				}
				setProgress(100, 0, false)
				setOnlyAlertOnce(true)
				setStyle(NotificationCompat.BigTextStyle().bigText(""))
			} else {
				setAutoCancel(true)
			}
		}
	}

	private fun updateProgressNotificationStrings() {
		val contentTitle = progressNotificationTitle.resolve(applicationContext)
		val statusString = currentStatus.resolve(applicationContext)
		val notification = progressNotificationBuilder
			.setContentTitle(contentTitle)
			.setContentText(statusString)
			.setStyle(NotificationCompat.BigTextStyle().bigText(statusString))
			.clearActions()
			.addAction(
				android.R.drawable.ic_delete,
				applicationContext.getString(R.string.button_text_abort),
				cancelIntent
			)
			.build()
		notificationManager?.notify(progressNotificationId, notification)
	}
}