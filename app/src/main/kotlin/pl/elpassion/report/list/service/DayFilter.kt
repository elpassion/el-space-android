package pl.elpassion.report.list.service

import pl.elpassion.report.list.Day

interface DayFilter {
    fun fetchFilteredDays(days: List<Day>): List<Day>
}