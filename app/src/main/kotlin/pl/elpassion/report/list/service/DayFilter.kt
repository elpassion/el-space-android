package pl.elpassion.report.list.service

import pl.elpassion.report.list.Day

interface DayFilter {
    fun filterOnly(days: List<Day>): List<Day>
}