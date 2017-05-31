package pl.elpassion.elspace.common

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun inflate(viewGroup: ViewGroup, @LayoutRes layoutRes: Int): View {
    return LayoutInflater.from(viewGroup.context).inflate(layoutRes, viewGroup, false)
}