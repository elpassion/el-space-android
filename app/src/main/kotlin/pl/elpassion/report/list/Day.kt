package pl.elpassion.report.list

import pl.elpassion.report.DayReport
import pl.elpassion.report.HoursReport

data class RegularDay(override val uuid: Long,
        override val name: String,
        override val date: String,
        val reports: List<HoursReport>,
        override val hasPassed: Boolean,
        val reportedHours: Double = reports.sumByDouble { it.reportedHours },
        override val isWeekendDay: Boolean) : Day

fun RegularDay.isNotFilledIn(): Boolean = hasPassed && reports.isEmpty()

data class ClosedDay(override val uuid: Long,
                     override val name: String,
                     override val date: String,
                     override val hasPassed: Boolean,
                     override val isWeekendDay: Boolean,
                     val report: DayReport) : Day

interface Day {
    val uuid: Long
    val name: String
    val date: String
    val hasPassed: Boolean
    val isWeekendDay: Boolean
}
fun createDayUUid(year: Int, monthIndex: Int, dayNumber: Int) = (((year + 1) * 10000) + ((monthIndex + 1) * 100) + dayNumber).toLong()

