package ru.solrudev.okkeipatcher.ui.core

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Returns a property delegate to access [FeatureViewModel] scoped to this [Fragment] and [binds][bind] it.
 *
 * If you have [derived views][derivedView] in your fragment which should be bound to the fragment's [FeatureViewModel],
 * you can [bind][bindDerived] them by passing them to this delegate function.
 *
 * Example:
 * ```
 * val derivedView1 by derivedView { DerivedView1(viewBinding, viewModel) }
 * val derivedView2 by derivedView { DerivedView2(viewBinding, viewModel) }
 * val viewModel: MyFeatureViewModel by featureViewModels(MyFragment::derivedView1, MyFragment::derivedView2)
 * ```
 * or
 * ```
 * val viewModel: MyFeatureViewModel by featureViewModels(
 *     { myFragment -> myFragment.derivedView1 },
 *     { myFragment -> myFragment.derivedView2 }
 * )
 * ```
 *
 * @param derivedViewProducer function which returns view derived from this fragment. Derived view will be bound to the
 * created FeatureViewModel. Derived views are created with [derivedView] delegate.
 */
inline fun <reified VM : FeatureViewModel<E, S>, E : Event, S : UiState, V> V.featureViewModels(
	vararg derivedViewProducer: V.() -> FeatureView<S>
): Lazy<VM>
		where V : FeatureView<S>,
			  V : Fragment {
	val viewModelLazy = viewModels<VM>()
	return FragmentFeatureViewModelLazy(this, viewModelLazy, derivedViewProducer)
}

class FragmentFeatureViewModelLazy<out VM : FeatureViewModel<E, S>, in E : Event, S : UiState, in V>(
	private var fragment: V?,
	private val viewModelLazy: Lazy<VM>,
	private var derivedViewProducers: Array<out (V.() -> FeatureView<S>)>
) : Lazy<VM> by viewModelLazy, DefaultLifecycleObserver
		where V : FeatureView<S>,
			  V : Fragment {

	private val viewModel by viewModelLazy
	private var fragmentManager: FragmentManager? = null
	private var callback: FragmentManager.FragmentLifecycleCallbacks? = null

	init {
		fragment?.lifecycle?.addObserver(this)
	}

	override fun onCreate(owner: LifecycleOwner) {
		fragment?.let(::registerBindViewModelCallback)
	}

	override fun onDestroy(owner: LifecycleOwner) {
		owner.lifecycle.removeObserver(this)
		onDestroy()
	}

	private fun registerBindViewModelCallback(fragment: V) {
		if (this.callback != null) {
			return
		}
		val callback = BindViewModelCallback().also { this.callback = it }
		val fragmentManager = fragment.parentFragmentManager.also { this.fragmentManager = it }
		fragmentManager.registerFragmentLifecycleCallbacks(callback, false)
	}

	private fun onDestroy() {
		fragmentManager?.let { fragmentManager ->
			callback?.let(fragmentManager::unregisterFragmentLifecycleCallbacks)
		}
		fragment = null
		fragmentManager = null
		callback = null
	}

	private inner class BindViewModelCallback : FragmentManager.FragmentLifecycleCallbacks() {

		override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
			val fragment = this@FragmentFeatureViewModelLazy.fragment
			if (fragment !== f) {
				return
			}
			viewModel.bind(fragment)
			derivedViewProducers.forEach { derivedViewProducer ->
				viewModel.bindDerived(fragment, fragment.derivedViewProducer())
			}
		}
	}
}