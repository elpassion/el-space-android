package pl.elpassion.elspace.hub.report.list.adapter

import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import pl.elpassion.elspace.hub.report.list.adapter.items.*

fun addSeparators(adapters: List<StableItemAdapter<*>>) = mutableListOf<StableItemAdapter<*>>().apply {
    adapters.forEachIndexed { i, itemAdapter ->
        val previousItemAdapter = adapters.getOrNull(i - 1)
        if (previousItemAdapter != null
                && itemAdapter !is PaidVacationReportItemAdapter
                && itemAdapter !is RegularReportItemAdapter
                && itemAdapter !is WeekendDayItem
                && itemAdapter !is EmptyItemAdapter) {
            addAll(listOf(SeparatorItemAdapter(), itemAdapter))
        } else {
            add(itemAdapter)
        }
    }
}