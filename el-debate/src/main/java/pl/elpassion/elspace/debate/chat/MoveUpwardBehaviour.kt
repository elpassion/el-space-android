package pl.elpassion.elspace.debate.chat

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

class MoveUpwardBehaviour : AppBarLayout.ScrollingViewBehavior {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        return dependency is Snackbar.SnackbarLayout || super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View?, dependency: View): Boolean {
        val translationY = Math.min(0f, ViewCompat.getTranslationY(dependency) - dependency.height)
        ViewCompat.setTranslationY(child, translationY)
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View?, dependency: View) {
        ViewCompat.animate(child).translationY(0f).start()
        super.onDependentViewChanged(parent, child, dependency)
    }
}
