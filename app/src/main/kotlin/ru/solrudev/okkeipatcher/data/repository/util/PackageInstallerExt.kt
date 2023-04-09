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

package ru.solrudev.okkeipatcher.data.repository.util

import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.data.ConfirmationStrategy
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.data.notification
import io.github.solrudev.simpleinstaller.installPackage
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Result

suspend fun PackageInstaller.install(apkPath: Path, immediate: Boolean = false): Result {
	val result = installPackage(apkPath.toFile()) {
		if (immediate) {
			confirmationStrategy = ConfirmationStrategy.IMMEDIATE
		}
		notification {
			icon = R.drawable.ic_notification
		}
	}
	return when (result) {
		is InstallResult.Failure -> Result.failure(result.cause?.toString() ?: "")
		is InstallResult.Success -> Result.success()
	}
}