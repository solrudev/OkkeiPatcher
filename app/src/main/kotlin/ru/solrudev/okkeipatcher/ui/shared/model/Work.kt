package ru.solrudev.okkeipatcher.ui.shared.model

import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.model.WorkState
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

interface HasWork {
	val work: Work
}

interface WorkStateEventFactory<E : JetEvent> {

	fun running(status: LocalizedString, progressData: ProgressData): E
	fun failed(reason: LocalizedString, stackTrace: String): E
	fun succeeded(): E
	fun canceled(): E
	fun unknown(): E

	fun fromWorkState(workState: WorkState) = when (workState) {
		is WorkState.Running -> running(workState.status, workState.progressData)
		is WorkState.Failed -> failed(workState.reason, workState.stackTrace)
		is WorkState.Succeeded -> succeeded()
		is WorkState.Canceled -> canceled()
		is WorkState.Unknown -> unknown()
	}
}