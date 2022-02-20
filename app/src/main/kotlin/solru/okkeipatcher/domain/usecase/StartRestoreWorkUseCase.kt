package solru.okkeipatcher.domain.usecase

import androidx.work.WorkManager
import java.util.*

interface StartRestoreWorkUseCase {

	/**
	 * Starts new restore work.
	 *
	 * @return [WorkManager]'s work [UUID].
	 */
	operator fun invoke(): UUID
}