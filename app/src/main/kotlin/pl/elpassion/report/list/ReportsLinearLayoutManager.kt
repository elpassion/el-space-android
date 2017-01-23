package pl.elpassion.report.list

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView

class ReportsLinearLayoutManager(val context: Context) : LinearLayoutManager(context) {

    private val scroller = ReportsSmoothScroller(context)

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }

    private class ReportsSmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
            return boxStart - viewStart
        }
    }
}