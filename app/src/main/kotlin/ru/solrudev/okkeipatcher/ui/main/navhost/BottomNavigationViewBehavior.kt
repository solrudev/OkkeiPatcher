package ru.solrudev.okkeipatcher.ui.main.navhost

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior

class BottomNavigationViewBehavior<V : View> : HideBottomViewOnScrollBehavior<V> {

	@Suppress("UNUSED")
	constructor() : super()

	@Suppress("UNUSED")
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	private var ignoreScroll = false

	override fun onStartNestedScroll(
		coordinatorLayout: CoordinatorLayout,
		child: V,
		directTargetChild: View,
		target: View,
		nestedScrollAxes: Int,
		type: Int
	): Boolean {
		ignoreScroll = false
		return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes, type)
	}

	override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int) {
		ignoreScroll = false
	}

	override fun onNestedScroll(
		coordinatorLayout: CoordinatorLayout,
		child: V,
		target: View,
		dxConsumed: Int,
		dyConsumed: Int,
		dxUnconsumed: Int,
		dyUnconsumed: Int,
		type: Int,
		consumed: IntArray
	) {
		if (ignoreScroll) {
			return
		}
		super.onNestedScroll(
			coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed
		)
	}

	fun ignoreScroll() {
		ignoreScroll = true
	}
}