package ru.solrudev.okkeipatcher.domain.service.operation

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.core.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import kotlin.time.Duration.Companion.seconds

class MockOperation(
	private val isPatchedDao: Dao<Boolean>,
	private val tags: Set<String>
) : AbstractOperation<Unit>() {

	private val stepsCount = 5
	override val progressMax = stepsCount * 100

	override suspend fun invoke() {
		repeat(stepsCount) { stepIndex ->
			val step = stepIndex + 1
			status(LocalizedString.raw(step.toString().repeat(10)))
			delay(1.seconds)
			progressDelta(100)
		}
		isPatchedDao.persist(tags.contains("PatchWork"))
	}
}