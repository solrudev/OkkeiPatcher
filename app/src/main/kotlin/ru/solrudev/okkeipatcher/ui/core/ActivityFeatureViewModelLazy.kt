package ru.solrudev.okkeipatcher.ui.core

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Returns a property delegate to access [FeatureViewModel] scoped to this [ComponentActivity] and [binds][bind] it.
 *
 * If you have [derived views][derivedView] in your activity which should be bound to the activity's [FeatureViewModel],
 * you can [bind][bindDerived] them by passing them to this delegate function.
 *
 * Example:
 * ```
 * val derivedView1 by derivedView { DerivedView1(viewBinding, viewModel) }
 * val derivedView2 by derivedView { DerivedView2(viewBinding, viewModel) }
 * val viewModel: MyFeatureViewModel by featureViewModels(MyActivity::derivedView1, MyActivity::derivedView2)
 * ```
 * or
 * ```
 * val viewModel: MyFeatureViewModel by featureViewModels(
 *     { myActivity -> myActivity.derivedView1 },
 *     { myActivity -> myActivity.derivedView2 }
 * )
 * ```
 *
 * @param derivedViewProducer function which returns view derived from this activity. Derived view will be bound to the
 * created FeatureViewModel. Derived views are created with [derivedView] delegate.
 */
inline fun <reified VM : FeatureViewModel<E, S>, E : Event, S : UiState, V> V.featureViewModels(
	vararg derivedViewProducer: V.() -> FeatureView<S>
): Lazy<VM>
		where V : FeatureView<S>,
			  V : ComponentActivity {
	val viewModelLazy = viewModels<VM>()
	return ActivityFeatureViewModelLazy(this, viewModelLazy, derivedViewProducer)
}

class ActivityFeatureViewModelLazy<out VM : FeatureViewModel<E, S>, in E : Event, S : UiState, in V>(
	private var activity: V?,
	private val viewModelLazy: Lazy<VM>,
	private var derivedViewProducers: Array<out (V.() -> FeatureView<S>)>
) : Lazy<VM> by viewModelLazy, DefaultLifecycleObserver
		where V : FeatureView<S>,
			  V : ComponentActivity {

	private val viewModel by viewModelLazy

	init {
		activity?.lifecycle?.addObserver(this)
	}

	override fun onCreate(owner: LifecycleOwner) {
		val activity = this.activity ?: return
		viewModel.bind(activity)
		derivedViewProducers.forEach { derivedViewProducer ->
			viewModel.bindDerived(activity, activity.derivedViewProducer())
		}
	}

	override fun onDestroy(owner: LifecycleOwner) {
		owner.lifecycle.removeObserver(this)
		onDestroy()
	}

	private fun onDestroy() {
		activity = null
		derivedViewProducers = emptyArray()
	}
}