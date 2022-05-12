package ru.solrudev.okkeipatcher.ui.util.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.ui.model.ReactiveView
import ru.solrudev.okkeipatcher.ui.model.UiState

/**
 * Launches lifecycle-aware [Flow] of UI state objects collecting which will re-render view each time
 * new UI state is emitted.
 * @return Flow-collecting [Job].
 */
fun <State : UiState, View> View.launchRender(uiStateFlow: Flow<State>): Job
		where View : ReactiveView<State>,
			  View : Fragment {
	return viewLifecycleOwner.lifecycleScope.launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			uiStateFlow.collect(::render)
		}
	}
}