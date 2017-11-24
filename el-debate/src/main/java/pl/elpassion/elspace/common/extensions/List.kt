package pl.elpassion.elspace.common.extensions

import com.elpassion.android.commons.recycler.basic.WithStableId

fun <T : WithStableId> MutableList<T>.update(newItem: T): Int {
    val existingPosition = indexOfFirst { it.id == newItem.id }
    return if (existingPosition > -1) {
        this[existingPosition] = newItem
        existingPosition
    } else {
        val newPosition = indexOfLast { it.id < newItem.id } + 1
        add(newPosition, newItem)
        newPosition
    }
}
