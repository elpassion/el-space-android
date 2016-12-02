package pl.elpassion.report.list.adapter

import com.elpassion.android.commons.recycler.ItemAdapter
import pl.elpassion.report.list.adapter.items.DayItemAdapter
import pl.elpassion.report.list.adapter.items.DayNotFilledInItemAdapter
import pl.elpassion.report.list.adapter.items.ReportItemAdapter
import pl.elpassion.report.list.adapter.items.SeparatorItemAdapter

fun addSeparators(adapters: List<ItemAdapter<*>>) = mutableListOf<ItemAdapter<*>>().apply {
    adapters.forEachIndexed { i, itemAdapter ->
        val previousItemAdapter = adapters.getOrElse(i - 1, { SeparatorItemAdapter() })
        if (doesNeedSeparator(itemAdapter, previousItemAdapter)) {
            addAll(listOf(SeparatorItemAdapter(), itemAdapter))
        } else {
            add(itemAdapter)
        }
    }
}

private fun doesNeedSeparator(itemAdapter: ItemAdapter<*>, previousItemAdapter: ItemAdapter<*>) =
        areTwoItemsTheSameAndAreNotReportItem(itemAdapter, previousItemAdapter) || isSecondCombination(itemAdapter, previousItemAdapter)

private fun isSecondCombination(itemAdapter: ItemAdapter<*>, previousItemAdapter: ItemAdapter<*>) =
        isCurrentItemCorrect(itemAdapter) && isPreviousItemCorrect(previousItemAdapter)

private fun isPreviousItemCorrect(previousItemAdapter: ItemAdapter<*>) =
        listOf(DayItemAdapter::class.java, DayNotFilledInItemAdapter::class.java, ReportItemAdapter::class.java).contains(previousItemAdapter.javaClass)

private fun isCurrentItemCorrect(itemAdapter: ItemAdapter<*>) =
        listOf(DayItemAdapter::class.java, DayNotFilledInItemAdapter::class.java).contains(itemAdapter.javaClass)

private fun areTwoItemsTheSameAndAreNotReportItem(itemAdapter: ItemAdapter<*>, previousItemAdapter: ItemAdapter<*>) =
        itemAdapter.viewType == previousItemAdapter.viewType && itemAdapter !is ReportItemAdapter

