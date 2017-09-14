package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithoutReports

class DayFilterImpl : DayFilter {

    override fun fetchFilteredDays(days: List<AdapterItem>) = days.filter { it is DayWithoutReports && it.hasPassed && !it.isWeekend }

}