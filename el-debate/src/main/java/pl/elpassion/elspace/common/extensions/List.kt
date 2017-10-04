package pl.elpassion.elspace.common.extensions

import com.elpassion.android.commons.recycler.basic.WithStableId

fun <T : WithStableId> MutableList<T>.update(newItem: T) {
    val position = findItemPositionById(newItem)
    if (position > -1) {
        this[position] = newItem
    } else {
        val previousItemPosition = indexOfLast { it.id < newItem.id }
        add(previousItemPosition + 1, newItem)
    }
}

fun <T : WithStableId> MutableList<T>.findItemPositionById(item: T) = indexOfFirst { it.id == item.id }