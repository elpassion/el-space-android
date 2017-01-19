package pl.elpassion.report.list.service

import pl.elpassion.report.list.Day
import pl.elpassion.report.list.DayWithoutReports

class DayFilterImpl : DayFilter {

    override fun fetchFilteredDays(days: List<Day>) = days.filter { it is DayWithoutReports && it.hasPassed && !it.isWeekend }

}