package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.report.list.AdapterItem

interface DayFilter {
    fun fetchFilteredDays(days: List<AdapterItem>): List<AdapterItem>
}