package pl.elpassion.report.list

data class Day(val dayNumber: Int, val reports: List<Report>, val hasPassed: Boolean, val reportedHours: Double = reports.sumByDouble { it.reportedHours }, val isWeekendDay: Boolean)

fun Day.isNotFilledIn(): Boolean = this.hasPassed && this.reports.isEmpty()