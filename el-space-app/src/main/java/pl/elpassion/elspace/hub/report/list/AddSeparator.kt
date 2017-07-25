package pl.elpassion.elspace.hub.report.list

import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport


fun addSeparators(items: List<AdapterItem>) = mutableListOf<AdapterItem>().apply {
    items.forEachIndexed { i, currentItem ->
        val previousItem = items.getOrNull(i - 1)
        if (previousItem != null
                && currentItem !is RegularHourlyReport
                && currentItem !is PaidVacationHourlyReport) {
            add(Separator())
            add(currentItem)
        } else {
            add(currentItem)
        }
    }
}