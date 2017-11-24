package pl.elpassion.elspace.common.extensions

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

fun RecyclerView.getVisibleItemsPositions(): IntRange {
    (layoutManager as LinearLayoutManager).run {
        val firstVisibleItemPosition = findFirstCompletelyVisibleItemPosition()
        val lastVisibleItemPosition = findLastCompletelyVisibleItemPosition()
        return if (firstVisibleItemPosition > -1) {
            firstVisibleItemPosition..lastVisibleItemPosition
        } else {
            IntRange.EMPTY
        }
    }
}