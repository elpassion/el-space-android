package pl.elpassion.elspace.common.extensions

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


fun RecyclerView.getVisibleItemsIds(): LongRange {
    run {
        (layoutManager as LinearLayoutManager).run {
            val firstVisibleItemPosition = findFirstCompletelyVisibleItemPosition()
            val lastVisibleItemPosition = findLastCompletelyVisibleItemPosition()
            return if (firstVisibleItemPosition > -1) {
                val firstVisibleCommentId = adapter.getItemId(firstVisibleItemPosition)
                val lastVisibleCommentId = adapter.getItemId(lastVisibleItemPosition)
                firstVisibleCommentId..lastVisibleCommentId
            } else {
                LongRange.EMPTY
            }
        }
    }
}