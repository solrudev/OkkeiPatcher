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

package ru.solrudev.okkeipatcher.domain.model.exception

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result

abstract class DomainException(val reason: LocalizedString) : Exception()

class NoNetworkException : DomainException(
	LocalizedString.resource(R.string.error_no_network)
)

class GameNotFoundException : DomainException(
	LocalizedString.resource(R.string.error_game_not_found)
)

class NotTrustworthyApkException : DomainException(
	LocalizedString.resource(R.string.error_not_trustworthy_apk)
)

class ApkNotFoundException : DomainException(
	LocalizedString.resource(R.string.error_apk_not_found)
)

class ObbNotFoundException : DomainException(
	LocalizedString.resource(R.string.error_obb_not_found)
)

class UninstallException : DomainException(
	LocalizedString.resource(R.string.error_uninstall)
)

class InstallException(failureCause: LocalizedString) : DomainException(
	LocalizedString.resource(R.string.error_install, failureCause)
)

class ScriptsCorruptedException : DomainException(
	LocalizedString.resource(R.string.error_hash_scripts_mismatch)
)

class ObbCorruptedException : DomainException(
	LocalizedString.resource(R.string.error_hash_obb_mismatch)
)

class AppUpdateCorruptedException : DomainException(
	LocalizedString.resource(R.string.error_update_app_corrupted)
)

/**
 * Catches and wraps _only_ [DomainExceptions][DomainException] as [Result.Failure].
 */
inline fun wrapDomainExceptions(block: () -> Unit) = try {
	block()
	Result.success()
} catch (e: DomainException) {
	Result.failure(e.reason)
}