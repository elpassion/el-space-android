package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.report.list.Day
import pl.elpassion.elspace.hub.report.list.DayWithoutReports

class DayFilterImpl : DayFilter {

    override fun fetchFilteredDays(days: List<Day>) = days.filter { it is DayWithoutReports && it.hasPassed && !it.isWeekend }

}