package ru.solrudev.okkeipatcher.domain.operation

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import kotlin.time.Duration.Companion.seconds

private const val STEPS_COUNT = 5

@Suppress("FunctionName")
fun MockOperation(
	patchRepository: PatchRepository,
	patchVersion: Persistable<String>,
	patchStatus: Persistable<Boolean>,
	isPatchWork: Boolean
) = operation(progressMax = STEPS_COUNT * 100) {
	repeat(STEPS_COUNT) { stepIndex ->
		val step = stepIndex + 1
		status(step.toString().repeat(10))
		delay(1.seconds)
		progressDelta(100)
	}
	if (isPatchWork) {
		patchVersion.persist(patchRepository.getDisplayVersion())
	} else {
		patchVersion.clear()
	}
	patchStatus.persist(isPatchWork)
	Result.success()
}