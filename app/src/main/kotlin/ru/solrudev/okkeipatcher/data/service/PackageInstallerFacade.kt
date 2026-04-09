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

@file:Suppress("ConstPropertyName", "UNUSED")

package ru.solrudev.okkeipatcher.data.service

import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import okio.Path
import ru.solrudev.ackpine.installer.PackageInstaller
import ru.solrudev.ackpine.installer.createSession
import ru.solrudev.ackpine.resources.ResolvableString
import ru.solrudev.ackpine.session.Session
import ru.solrudev.ackpine.session.await
import ru.solrudev.ackpine.session.parameters.Confirmation
import ru.solrudev.ackpine.session.parameters.DrawableId
import ru.solrudev.ackpine.session.parameters.notification
import ru.solrudev.ackpine.shizuku.shizuku
import ru.solrudev.ackpine.uninstaller.PackageUninstaller
import ru.solrudev.ackpine.uninstaller.createSession
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.shizuku.ShizukuAvailabilityFlow
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.Result
import javax.inject.Inject

interface PackageInstallerFacade {
	suspend fun install(apkPath: Path, appName: String, immediate: Boolean = false): Result<Unit>
	suspend fun uninstall(packageName: String, appName: String): Result<Unit>
}

class PackageInstallerFacadeImpl @Inject constructor(
	private val packageInstaller: PackageInstaller,
	private val packageUninstaller: PackageUninstaller,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	preferencesRepository: PreferencesRepository
) : PackageInstallerFacade {

	private val coroutineScope = CoroutineScope(ioDispatcher)

	private val isShizukuAvailable = ShizukuAvailabilityFlow(preferencesRepository.isShizukuEnabled.flow)
		.stateIn(coroutineScope, SharingStarted.Lazily, initialValue = null)
		.filterNotNull()

	override suspend fun install(apkPath: Path, appName: String, immediate: Boolean): Result<Unit> {
		val session = packageInstaller.createSession(apkPath.toFile().toUri()) {
			if (immediate) {
				confirmation = Confirmation.IMMEDIATE
			}
			notification {
				icon = NotificationIcon
				title = NotificationTitleInstall
				contentText = NotificationMessageInstall(appName)
			}
			if (isShizukuAvailable.first()) {
				shizuku {
					bypassLowTargetSdkBlock = true
					replaceExisting = true
				}
			}
		}
		return when (val result = session.await()) {
			Session.State.Succeeded -> Result.success()
			is Session.State.Failed -> Result.failure(result.failure.message.orEmpty())
		}
	}

	override suspend fun uninstall(packageName: String, appName: String): Result<Unit> {
		val session = packageUninstaller.createSession(packageName) {
			notification {
				icon = NotificationIcon
				title = NotificationTitleUninstall
				contentText = NotificationMessageUninstall(appName)
			}
			if (isShizukuAvailable.first()) {
				shizuku()
			}
		}
		return when (val result = session.await()) {
			Session.State.Succeeded -> Result.success()
			is Session.State.Failed -> Result.failure(result.failure.message.orEmpty())
		}
	}
}

private data object NotificationIcon : DrawableId {
	private const val serialVersionUID = -4953042512419246108L
	private fun readResolve(): Any = NotificationIcon
	override fun drawableId() = R.drawable.ic_notification
}

private object NotificationTitleInstall : ResolvableString.Resource() {
	private const val serialVersionUID = 2562954299038490907L
	private fun readResolve(): Any = NotificationTitleInstall
	override fun stringId() = R.string.notification_title_install
}

private class NotificationMessageInstall(appName: String) : ResolvableString.Resource(appName) {
	override fun stringId() = R.string.notification_message_install
	private companion object {
		private const val serialVersionUID = 625651879829700570L
	}
}

private object NotificationTitleUninstall : ResolvableString.Resource() {
	private const val serialVersionUID = -4596819327544505895L
	private fun readResolve(): Any = NotificationTitleUninstall
	override fun stringId() = R.string.notification_title_uninstall
}

private class NotificationMessageUninstall(appName: String) : ResolvableString.Resource(appName) {
	override fun stringId() = R.string.notification_message_uninstall
	private companion object {
		private const val serialVersionUID = 2870555503021692614L
	}
}