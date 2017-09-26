package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithoutReports
import pl.elpassion.elspace.hub.report.list.adapter.Empty
import pl.elpassion.elspace.hub.report.list.shouldHaveReports

class DayFilterImpl : DayFilter {

    override fun fetchFilteredDays(days: List<AdapterItem>) = days.filter { it is DayWithoutReports && it.shouldHaveReports() || it is Empty }

}