package pl.elpassion.report.list.adapter

import com.elpassion.android.commons.recycler.StableItemAdapter
import pl.elpassion.report.list.adapter.items.*

fun addSeparators(adapters: List<StableItemAdapter<*>>) = mutableListOf<StableItemAdapter<*>>().apply {
    adapters.forEachIndexed { i, itemAdapter ->
        val previousItemAdapter = adapters.getOrNull(i - 1)
        if (areTwoItemsTheSame(itemAdapter, previousItemAdapter) ||
                itemAdapter is DayItemAdapter && previousItemAdapter is DayNotFilledInItemAdapter ||
                itemAdapter is DayItemAdapter && previousItemAdapter is ReportItemAdapter ||
                itemAdapter is DayNotFilledInItemAdapter && previousItemAdapter is DayItemAdapter ||
                itemAdapter is DayNotFilledInItemAdapter && previousItemAdapter is ReportItemAdapter ||
                itemAdapter is DayNotFilledInItemAdapter && previousItemAdapter is DayWithDailyReportsItemAdapter) {

            addAll(listOf(SeparatorItemAdapter(), itemAdapter))
        } else {
            add(itemAdapter)
        }
    }
}

private fun areTwoItemsTheSame(itemAdapter: StableItemAdapter<*>, previousItemAdapter: StableItemAdapter<*>?): Boolean {
    return itemAdapter.viewType == previousItemAdapter?.viewType && itemAdapter !is ReportItemAdapter && itemAdapter !is WeekendDayItem
}
