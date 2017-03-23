package pl.elpassion.elspace.common.extensions

import android.view.Menu
import android.view.MenuItem

val Menu.items: List<MenuItem>
    get() = (0 until size()).map { getItem(it) }