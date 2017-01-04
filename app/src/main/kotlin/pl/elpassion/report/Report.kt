package pl.elpassion.report

import pl.elpassion.common.extensions.getDateString
import pl.elpassion.project.Project
import java.io.Serializable

interface Report {
    val id: Long
    val year: Int
    val month: Int
    val day: Int

    val date: String
        get() = getDateString(year, month, day)
}

interface HourlyReport : Report {
    val reportedHours: Double
}

data class RegularHourlyReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        override val reportedHours: Double,
        val project: Project,
        val description: String) : Serializable, HourlyReport

data class PaidVacationHourlyReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        override val reportedHours: Double) : Serializable, HourlyReport

data class DailyReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        val reportType: DailyReportType) : Serializable, Report

enum class DailyReportType(val description: String) {
    UNPAID_VACATIONS("UnpaidVacation"),
    SICK_LEAVE("SickLeave")
}
