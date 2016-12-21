package pl.elpassion.report.list.adapter

import com.elpassion.android.commons.recycler.StableItemAdapter
import com.elpassion.android.commons.recycler.StableRecyclerViewAdapter


class ReportsAdapter() : StableRecyclerViewAdapter() {
    fun updateAdapter(items: List<StableItemAdapter<*>>) {
        adapters.clear()
        adapters.addAll(items)
        notifyDataSetChanged()
    }
}