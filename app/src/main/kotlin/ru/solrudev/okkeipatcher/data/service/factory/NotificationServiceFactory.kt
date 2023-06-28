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

package ru.solrudev.okkeipatcher.data.service.factory

import android.app.PendingIntent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.service.NotificationService
import ru.solrudev.okkeipatcher.data.service.NotificationServiceImpl
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import javax.inject.Inject

interface NotificationServiceFactory {

	fun create(
		progressNotificationTitle: LocalizedString,
		contentIntent: PendingIntent,
		showGameIconInProgressNotification: Boolean
	): NotificationService
}

class NotificationServiceFactoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : NotificationServiceFactory {

	override fun create(
		progressNotificationTitle: LocalizedString,
		contentIntent: PendingIntent,
		showGameIconInProgressNotification: Boolean
	): NotificationService = NotificationServiceImpl(
		applicationContext,
		progressNotificationTitle,
		contentIntent,
		showGameIconInProgressNotification
	)
}