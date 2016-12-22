package pl.elpassion.report.list.adapter

import com.elpassion.android.commons.recycler.StableItemAdapter
import pl.elpassion.report.list.adapter.items.ReportItemAdapter
import pl.elpassion.report.list.adapter.items.SeparatorItemAdapter
import pl.elpassion.report.list.adapter.items.WeekendDayItem

fun addSeparators(adapters: List<StableItemAdapter<*>>) = mutableListOf<StableItemAdapter<*>>().apply {
    adapters.forEachIndexed { i, itemAdapter ->
        val previousItemAdapter = adapters.getOrNull(i - 1)
        if (!(previousItemAdapter == null
                || itemAdapter is ReportItemAdapter
                || itemAdapter is WeekendDayItem)) {
            addAll(listOf(SeparatorItemAdapter(), itemAdapter))
        } else {
            add(itemAdapter)
        }
    }
}