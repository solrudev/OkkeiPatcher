/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
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
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.ForegroundInfo
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

private val globalProgressNotificationId = AtomicInteger(Random.nextInt(from = 10000, until = 1000000))
private val globalMessageNotificationId = AtomicInteger(Random.nextInt(from = 10000, until = 1000000))

interface NotificationService {
	fun createForegroundInfo(): ForegroundInfo
	fun updateProgressNotification(status: LocalizedString, progressData: ProgressData)
	fun displayMessageNotification(message: Message)
}

class NotificationServiceImpl(
	private val applicationContext: Context,
	private val progressNotificationTitle: LocalizedString,
	private val contentIntent: PendingIntent
) : NotificationService {

	private val progressNotificationBuilder =
		createNotificationBuilder(progressNotificationTitle, progressNotification = true)

	private val progressNotificationId = globalProgressNotificationId.incrementAndGet()
	private val notificationManager = applicationContext.getSystemService<NotificationManager>()

	override fun createForegroundInfo() = ForegroundInfo(progressNotificationId, progressNotificationBuilder.build())

	override fun updateProgressNotification(status: LocalizedString, progressData: ProgressData) {
		val titleString = progressNotificationTitle.resolve(applicationContext)
		val statusString = status.resolve(applicationContext)
		val notification = progressNotificationBuilder
			.setContentTitle(titleString)
			.setContentText(statusString)
			.setProgress(progressData.max, progressData.progress, false)
			.build()
		notificationManager?.notify(progressNotificationId, notification)
	}

	override fun displayMessageNotification(message: Message) {
		val messageString = message.text.resolve(applicationContext)
		val notification = createNotificationBuilder(message.title, progressNotification = false).apply {
			setContentText(messageString)
			if (messageString.length > 28) {
				setStyle(NotificationCompat.BigTextStyle().bigText(messageString))
			}
		}.build()
		val notificationId = globalMessageNotificationId.incrementAndGet()
		notificationManager?.notify(notificationId, notification)
	}

	private fun createNotificationBuilder(
		title: LocalizedString,
		progressNotification: Boolean
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
			setSound(null)
			if (progressNotification) {
				setProgress(100, 0, false)
				setOnlyAlertOnce(true)
			} else {
				setAutoCancel(true)
			}
		}
	}
}