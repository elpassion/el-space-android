package pl.elpassion.elspace.hub.report.list

import pl.elpassion.elspace.hub.report.RegularHourlyReport


fun addSeparators(items: List<AdapterItem>) = mutableListOf<AdapterItem>().apply {
    items.forEachIndexed { i, currentItem ->
        val previousItemAdapter = items.getOrNull(i - 1)
        if (previousItemAdapter != null
                && currentItem !is RegularHourlyReport) {
            add(Separator())
            add(currentItem)
        } else {
            add(currentItem)
        }
    }
}