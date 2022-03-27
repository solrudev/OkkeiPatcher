package solru.okkeipatcher.domain.gamefile

import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.operation.Operation

interface Patchable {
	fun canPatch(onNegative: (message: LocalizedString) -> Unit = {}): Boolean
	fun patch(): Operation<Unit>
	fun update(): Operation<Unit>
}