package solru.okkeipatcher.domain.usecase

import androidx.work.WorkManager
import java.util.*

interface GetPatchWorkUuidUseCase {

	/**
	 * @return [WorkManager]'s work [UUID] and `null` if it is not started yet.
	 */
	operator fun invoke(): UUID?
}