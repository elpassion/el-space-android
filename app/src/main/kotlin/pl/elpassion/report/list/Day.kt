package pl.elpassion.report.list

import pl.elpassion.report.DailyReport
import pl.elpassion.report.HourlyReport

data class DayWithHourlyReports(
        override val uuid: Long,
        override val name: String,
        override val date: String,
        val reports: List<HourlyReport>,
        override val hasPassed: Boolean,
        val reportedHours: Double = reports.sumByDouble { it.reportedHours }) : Day

data class DayWithDailyReport(override val uuid: Long,
                              override val name: String,
                              override val date: String,
                              override val hasPassed: Boolean,
                              val report: DailyReport) : Day



data class DayWithoutReports(override val uuid: Long,
                             override val name: String,
                             override val date: String,
                             override val hasPassed: Boolean,
                             val isWeekend: Boolean) : Day

fun DayWithoutReports.shouldHaveReports() = !isWeekend && hasPassed

interface Day {
    val uuid: Long
    val name: String
    val date: String
    val hasPassed: Boolean
}
fun createDayUUid(year: Int, monthIndex: Int, dayNumber: Int) = (((year + 1) * 10000) + ((monthIndex + 1) * 100) + dayNumber).toLong()

