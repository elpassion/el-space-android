package pl.elpassion.report.list

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


@Suppress("UNUSED")
class ScrollAwareFABBehavior(context: Context, private val attributeSet: AttributeSet) : FloatingActionButton.Behavior() {
    private val toolbarHeight = context.getActionBarSize()

    override fun layoutDependsOn(parent: CoordinatorLayout, fab: FloatingActionButton, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, fab: FloatingActionButton, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            val lp = fab.layoutParams as CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = fab.height + fabBottomMargin
            val ratio = dependency.getY() / toolbarHeight.toFloat()
            fab.translationY = -distanceToScroll * ratio
        }
        return true
    }

    private fun Context.getActionBarSize(): Int {
        val typedValue = TypedValue().apply {
            theme.resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, this, true)
        }
        return TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }
}
