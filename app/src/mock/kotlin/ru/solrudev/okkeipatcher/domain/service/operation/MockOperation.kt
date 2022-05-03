package ru.solrudev.okkeipatcher.domain.service.operation

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.persistence.Dao
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