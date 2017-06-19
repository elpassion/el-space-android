package pl.elpassion.elspace.common

import android.annotation.TargetApi
import android.graphics.drawable.Animatable
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.loader.view.*
import pl.elpassion.R

fun showLoader(coordinatorLayout: CoordinatorLayout) {
    if (coordinatorLayout.loader == null) {
        val loaderRoot = inflate(coordinatorLayout, R.layout.loader)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Animations.areEnabled) {
            startAnimation(loaderRoot)
        }
        coordinatorLayout.addView(loaderRoot)
    }
}

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

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun showVoteLoader(loaderRoot: ImageView) {
    loaderRoot.visibility = View.VISIBLE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Animations.areEnabled) {
        (loaderRoot.drawable as? Animatable)?.start()
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun hideVoteLoader(loaderRoot: ImageView) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Animations.areEnabled) {
        (loaderRoot.drawable as? Animatable)?.stop()
    }
    loaderRoot.visibility = View.GONE
}