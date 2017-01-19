package pl.elpassion.common

import android.annotation.TargetApi
import android.graphics.drawable.Animatable
import android.os.Build
import android.provider.Settings
import android.support.design.widget.CoordinatorLayout
import android.view.View
import kotlinx.android.synthetic.main.loader.view.*
import pl.elpassion.R

fun showLoader(coordinatorLayout: CoordinatorLayout) {
    if (coordinatorLayout.loader == null) {
        val loaderRoot = inflate(coordinatorLayout, R.layout.loader)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isAnimationEnabled(coordinatorLayout)) {
            startAnimation(loaderRoot)
        }
        coordinatorLayout.addView(loaderRoot)
    }
}

private fun isAnimationEnabled(coordinatorLayout: CoordinatorLayout) =
        Settings.Global.getInt(coordinatorLayout.context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1) != 0

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun startAnimation(loaderRoot: View) {
    (loaderRoot.loaderImage.drawable as? Animatable)?.start()
}

fun hideLoader(coordinatorLayout: CoordinatorLayout) {
    coordinatorLayout.loader?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stopAnimation(it)
        }
        coordinatorLayout.removeView(it)
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun stopAnimation(loaderRoot: View) {
    (loaderRoot.loaderImage.drawable as? Animatable)?.stop()
}
