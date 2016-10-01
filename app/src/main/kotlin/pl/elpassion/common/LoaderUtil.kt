package pl.elpassion.common

import android.support.design.widget.CoordinatorLayout
import pl.elpassion.R

fun showLoader(layout: CoordinatorLayout) {
    if (layout.findViewById(R.id.loader) == null) {
        val loaderRoot = inflate(layout, R.layout.loader)
        layout.addView(loaderRoot)
    }
}


fun hideLoader(layout: CoordinatorLayout) {
    val loader = layout.findViewById(R.id.loader)
    if (loader != null) {
        layout.removeView(loader)
    }
}