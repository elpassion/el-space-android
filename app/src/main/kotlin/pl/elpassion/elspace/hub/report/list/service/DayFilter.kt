package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.report.list.Day

interface DayFilter {
    fun fetchFilteredDays(days: List<Day>): List<Day>
}