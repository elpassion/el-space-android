package pl.elpassion.report.list

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import android.view.View.INVISIBLE


@Suppress("UNUSED")
class ScrollAwareFABBehavior(private val context: Context, private val attributeSet: AttributeSet) : CoordinatorLayout.Behavior<FloatingActionButton>() {

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?,
                                fab: FloatingActionButton,
                                target: View?, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, fab, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyConsumed > 0 && fab.visibility == View.VISIBLE) {
            fab.hideFab()
        } else if (dyConsumed < 0 && fab.visibility != View.VISIBLE) {
            fab.show()
        }
    }

    private fun FloatingActionButton.hideFab() {
        hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton) {
                fab.visibility = INVISIBLE
            }
        })
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?,
                                     directTargetChild: View?, target: View?, nestedScrollAxes: Int) = true
}