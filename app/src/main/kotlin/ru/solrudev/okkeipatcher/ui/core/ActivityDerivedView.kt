package ru.solrudev.okkeipatcher.ui.core

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

/**
 * Returns a property delegate for accessing [FeatureView] which is derived from the current activity (i.e. sharing
 * its [UiState] and [FeatureViewModel]).
 *
 * Example:
 * ```
 * class SomeView(
 *     val viewBinding: MyLayoutBinding,
 *     val viewModel: MyFeatureViewModel
 * ) : FeatureView<MyUiState> { ... }
 * ...
 * val someView by derivedView { SomeView(viewBinding, viewModel) }
 * ```
 *
 * @param derivedViewProducer function returning derived view. It has parent view as its receiver.
 */
fun <DV : FeatureView<S>, S : UiState, V> V.derivedView(derivedViewProducer: V.() -> DV): DerivedViewProperty<V, S, DV>
		where V : FeatureView<S>,
			  V : ComponentActivity {
	return ActivityDerivedViewProperty(this, derivedViewProducer)
}

private class ActivityDerivedViewProperty<in V, in S : UiState, out DV : FeatureView<S>>(
	activity: V,
	private val derivedViewProducer: V.() -> DV
) : DerivedViewProperty<V, S, DV>, DefaultLifecycleObserver
		where V : FeatureView<S>,
			  V : ComponentActivity {

	private var derivedView: DV? = null

	init {
		activity.lifecycle.addObserver(this)
	}

	override fun onDestroy(owner: LifecycleOwner) {
		owner.lifecycle.removeObserver(this)
		onDestroy()
	}

	override fun getValue(thisRef: V, property: KProperty<*>): DV {
		return invoke(thisRef)
	}

	override fun invoke(thisRef: V): DV {
		checkActivityLifecycle(thisRef)
		return derivedView ?: thisRef.derivedViewProducer().also { derivedView = it }
	}

	private fun onDestroy() {
		derivedView = null
	}

	private fun checkActivityLifecycle(thisRef: V) {
		if (thisRef.isDestroyed) {
			error("Accessing derived view in activity after onDestroy().")
		}
	}
}