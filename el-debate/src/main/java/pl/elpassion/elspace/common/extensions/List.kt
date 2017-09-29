package pl.elpassion.elspace.common.extensions

import com.elpassion.android.commons.recycler.basic.WithStableId

fun <T : WithStableId> MutableList<T>.update(newItem: T) {
    val position = indexOfFirst { it.id == newItem.id }
    run {
        if (position > -1) {
            removeAt(position)
            add(position, newItem)
        } else {
            val previousItemPosition = indexOfLast { it.id < newItem.id }
            add(previousItemPosition + 1, newItem)
        }
    }
}