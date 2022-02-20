package solru.okkeipatcher.domain.usecase

import androidx.work.WorkManager
import java.util.*

interface StartPatchWorkUseCase {

	/**
	 * Starts new patch work.
	 *
	 * @return [WorkManager]'s work [UUID].
	 */
	operator fun invoke(): UUID
}