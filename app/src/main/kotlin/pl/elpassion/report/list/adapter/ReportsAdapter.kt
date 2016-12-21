package pl.elpassion.report.list.adapter

import com.elpassion.android.commons.recycler.BaseRecyclerViewAdapter
import com.elpassion.android.commons.recycler.ItemAdapter


class ReportsAdapter() : BaseRecyclerViewAdapter() {
    fun updateAdapter(items: List<ItemAdapter<*>>) {
        adapters.clear()
        adapters.addAll(items)
        notifyDataSetChanged()
    }
}