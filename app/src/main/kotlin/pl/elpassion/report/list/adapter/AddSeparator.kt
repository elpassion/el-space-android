package pl.elpassion.report.list.adapter

import com.elpassion.android.commons.recycler.StableItemAdapter
import pl.elpassion.report.list.adapter.items.*

fun addSeparators(adapters: List<StableItemAdapter<*>>) = mutableListOf<StableItemAdapter<*>>().apply {
    adapters.forEachIndexed { i, itemAdapter ->
        val previousItemAdapter = adapters.getOrNull(i - 1)
        if (previousItemAdapter != null
                && itemAdapter !is PaidVacationReportItemAdapter
                && itemAdapter !is RegularHourlyReportItemAdapter
                && itemAdapter !is WeekendDayItem
                && itemAdapter !is EmptyItemAdapter) {
            addAll(listOf(SeparatorItemAdapter(), itemAdapter))
        } else {
            add(itemAdapter)
        }
    }
}