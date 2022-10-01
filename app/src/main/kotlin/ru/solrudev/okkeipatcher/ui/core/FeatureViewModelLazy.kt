package ru.solrudev.okkeipatcher.ui.core

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Returns a property delegate to access [FeatureViewModel] scoped to this [ComponentActivity] and [binds][bind] it.
 */
inline fun <reified VM : FeatureViewModel<E, S>, E : Event, S : UiState, V> V.featureViewModels(): Lazy<VM>
		where V : FeatureView<S>,
			  V : ComponentActivity {
	val viewModelLazy = viewModels<VM>()
	return ActivityFeatureViewModelLazy(this, viewModelLazy)
}

/**
 * Returns a property delegate to access [FeatureViewModel] scoped to this [Fragment] and [binds][bind] it.
 */
inline fun <reified VM : FeatureViewModel<E, S>, E : Event, S : UiState, V> V.featureViewModels(): Lazy<VM>
		where V : FeatureView<S>,
			  V : Fragment {
	val viewModelLazy = viewModels<VM>()
	return FragmentFeatureViewModelLazy(this, viewModelLazy)
}

class ActivityFeatureViewModelLazy<VM : FeatureViewModel<E, S>, E : Event, S : UiState, V>(
	private var activity: V?,
	private val viewModelLazy: Lazy<VM>
) : Lazy<VM> by viewModelLazy, DefaultLifecycleObserver
		where V : FeatureView<S>,
			  V : ComponentActivity {

	init {
		activity?.lifecycle?.addObserver(this)
	}

	override fun onCreate(owner: LifecycleOwner) {
		activity?.let {
			viewModelLazy.value.bind(it)
		}
	}

	override fun onDestroy(owner: LifecycleOwner) {
		owner.lifecycle.removeObserver(this)
		activity = null
	}
}

class FragmentFeatureViewModelLazy<VM : FeatureViewModel<E, S>, E : Event, S : UiState, V>(
	private var fragment: V?,
	private val viewModelLazy: Lazy<VM>
) : Lazy<VM> by viewModelLazy, DefaultLifecycleObserver
		where V : FeatureView<S>,
			  V : Fragment {

	private var fragmentManager: FragmentManager? = null
	private var callback: FragmentLifecycleCallbacks? = null

	init {
		fragment?.lifecycle?.addObserver(this)
	}

	override fun onCreate(owner: LifecycleOwner) {
		fragment?.let(::registerBindingCallback)
	}

	override fun onDestroy(owner: LifecycleOwner) {
		owner.lifecycle.removeObserver(this)
		onDestroy()
	}

	private fun registerBindingCallback(fragment: V) {
		if (callback != null) {
			return
		}
		val callback = BindViewModelFragmentCallback<S, V>().also { this.callback = it }
		val fragmentManager = fragment.parentFragmentManager.also { this.fragmentManager = it }
		fragmentManager.registerFragmentLifecycleCallbacks(callback, false)
	}

	private fun onDestroy() {
		fragmentManager?.let { fragmentManager ->
			callback?.let(fragmentManager::unregisterFragmentLifecycleCallbacks)
		}
		fragmentManager = null
		callback = null
	}

	private inner class BindViewModelFragmentCallback<S : UiState, V> : FragmentLifecycleCallbacks()
			where V : FeatureView<S>,
				  V : Fragment {

		override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
			fragment
				?.takeIf { it === f }
				?.let { fragment ->
					viewModelLazy.value.bind(fragment)
				}
		}
	}
}