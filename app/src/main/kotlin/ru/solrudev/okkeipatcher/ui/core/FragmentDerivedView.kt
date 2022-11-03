package ru.solrudev.okkeipatcher.ui.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

/**
 * Returns a property delegate for accessing [FeatureView] which is derived from the current fragment (i.e. sharing
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
			  V : Fragment {
	return FragmentDerivedViewProperty(this, derivedViewProducer)
}

private class FragmentDerivedViewProperty<in V, in S : UiState, out DV : FeatureView<S>>(
	private var fragment: V?,
	private val derivedViewProducer: V.() -> DV
) : DerivedViewProperty<V, S, DV>, DefaultLifecycleObserver
		where V : FeatureView<S>,
			  V : Fragment {

	private var derivedView: DV? = null
	private var fragmentManager: FragmentManager? = null
	private var callback: FragmentManager.FragmentLifecycleCallbacks? = null

	init {
		fragment?.lifecycle?.addObserver(this)
	}

	override fun onCreate(owner: LifecycleOwner) {
		fragment?.let(::registerViewDestroyedCallback)
	}

	override fun onDestroy(owner: LifecycleOwner) {
		owner.lifecycle.removeObserver(this)
		onDestroy()
	}

	override fun getValue(thisRef: V, property: KProperty<*>): DV {
		return invoke(thisRef)
	}

	override fun invoke(thisRef: V): DV {
		checkFragmentLifecycle(thisRef)
		return derivedView ?: thisRef.derivedViewProducer().also { derivedView = it }
	}

	private fun registerViewDestroyedCallback(fragment: V) {
		if (this.callback != null) {
			return
		}
		val callback = ViewDestroyedCallback().also { this.callback = it }
		val fragmentManager = fragment.parentFragmentManager.also { this.fragmentManager = it }
		fragmentManager.registerFragmentLifecycleCallbacks(callback, false)
	}

	private fun onDestroy() {
		fragmentManager?.let { fragmentManager ->
			callback?.let(fragmentManager::unregisterFragmentLifecycleCallbacks)
		}
		derivedView = null
		fragment = null
		fragmentManager = null
		callback = null
	}

	private fun checkFragmentLifecycle(thisRef: V) {
		if (thisRef.view == null) {
			error("Accessing derived view in fragment before onViewCreated() or after onDestroyView().")
		}
	}

	private inner class ViewDestroyedCallback : FragmentManager.FragmentLifecycleCallbacks() {

		override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
			if (fragment !== f) {
				return
			}
			derivedView = null
		}
	}
}