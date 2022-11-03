package ru.solrudev.okkeipatcher.ui.core

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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
 * Launches lifecycle-aware collection of the [Flow] of [UiState] which will re-render derived view each time
 * new state is emitted.
 * @param parentView parent [FeatureView].
 * @param derivedView [FeatureView] derived from [parentView]. Created with `derivedView` delegate.
 * @return [Job] of the flow collection.
 */
fun <S : UiState, V> Flow<S>.bindDerived(parentView: V, derivedView: FeatureView<S>): Job
		where V : FeatureView<S>,
			  V : ComponentActivity {
	return parentView.lifecycleScope.launch {
		parentView.repeatOnLifecycle(Lifecycle.State.STARTED) {
			collect(derivedView::render)
		}
	}
}