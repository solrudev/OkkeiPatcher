package ru.solrudev.okkeipatcher.domain.service.operation

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import kotlin.time.Duration.Companion.seconds

private const val STEPS_COUNT = 5

@Suppress("FunctionName")
fun MockOperation(
	isPatched: Persistable<Boolean>,
	tags: Set<String>
) = operation(progressMax = STEPS_COUNT * 100) {
	repeat(STEPS_COUNT) { stepIndex ->
		val step = stepIndex + 1
		status(LocalizedString.raw(step.toString().repeat(10)))
		delay(1.seconds)
		progressDelta(100)
	}
	isPatched.persist(tags.contains("PatchWork"))
	Result.Success
}