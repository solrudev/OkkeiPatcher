package ru.solrudev.okkeipatcher.ui.core

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * A view which can render UI state object.
 */
interface FeatureView<in S : UiState> {
	fun render(uiState: S)
}

/**
 * Launches lifecycle-aware collection of the [Flow] of [UiState] which will re-render view each time
 * new state is emitted.
 * @return [Job] of the flow collection.
 */
fun <S : UiState, V> Flow<S>.bind(featureView: V): Job
		where V : FeatureView<S>,
			  V : Fragment {
	return featureView.viewLifecycleOwner.lifecycleScope.launch {
		featureView.viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			collect(featureView::render)
		}
	}
}

/**
 * Launches lifecycle-aware collection of the [Flow] of [UiState] which will re-render view each time
 * new state is emitted.
 * @return [Job] of the flow collection.
 */
fun <S : UiState, V> Flow<S>.bind(featureView: V): Job
		where V : FeatureView<S>,
			  V : ComponentActivity {
	return featureView.lifecycleScope.launch {
		featureView.repeatOnLifecycle(Lifecycle.State.STARTED) {
			collect(featureView::render)
		}
	}
}

/**
 * Launches lifecycle-aware collection of the [Flow] of [UiState] for non-UI fragment which will re-render it each
 * time new state is emitted.
 *
 * **Use only in non-UI fragments, as it doesn't respect fragment's view lifecycle.**
 * @return [Job] of the flow collection.
 */
fun <S : UiState, V> Flow<S>.bindHeadless(featureView: V): Job
		where V : FeatureView<S>,
			  V : Fragment {
	return featureView.lifecycleScope.launch {
		featureView.repeatOnLifecycle(Lifecycle.State.STARTED) {
			collect(featureView::render)
		}
	}
}