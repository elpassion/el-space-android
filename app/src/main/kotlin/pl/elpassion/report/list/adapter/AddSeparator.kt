package pl.elpassion.report.list.adapter

import com.elpassion.android.commons.recycler.StableItemAdapter
import pl.elpassion.report.list.adapter.items.*

fun addSeparators(adapters: List<StableItemAdapter<*>>) = mutableListOf<StableItemAdapter<*>>().apply {
    adapters.forEachIndexed { i, itemAdapter ->
        val previousItemAdapter = adapters.getOrNull(i - 1)
        if (previousItemAdapter != null && !areTwoItemsTheSame(itemAdapter, previousItemAdapter)){
            addAll(listOf(SeparatorItemAdapter(), itemAdapter))
        } else {
            add(itemAdapter)
        }
    }
}

private fun areTwoItemsTheSame(itemAdapter: StableItemAdapter<*>, previousItemAdapter: StableItemAdapter<*>): Boolean {
    return itemAdapter.viewType == previousItemAdapter.viewType
}
