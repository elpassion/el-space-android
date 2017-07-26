package pl.elpassion.elspace.hub.report.list

import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport


fun addSeparators(items: List<AdapterItem>) = mutableListOf<AdapterItem>().apply {
    add(Empty())
    items.forEachIndexed { i, currentItem ->
        val previousItem = items.getOrNull(i - 1)
        if (previousItem != null && (previousItem is DayWithoutReports && previousItem.isWeekend && currentItem is DayWithoutReports && currentItem.isWeekend)) {
            add(currentItem)
        } else if (previousItem != null
                && currentItem !is RegularHourlyReport
                && currentItem !is PaidVacationHourlyReport
                && currentItem !is Empty) {
            add(Separator())
            add(currentItem)
        } else {
            add(currentItem)
        }
    }
    add(Empty())
}