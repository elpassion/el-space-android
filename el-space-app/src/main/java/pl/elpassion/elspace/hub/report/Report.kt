package pl.elpassion.elspace.hub.report

import pl.elpassion.elspace.common.extensions.getDateString
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.list.AdapterItem
import java.io.Serializable

sealed class Report : Serializable {
    abstract val id: Long
    abstract val year: Int
    abstract val month: Int
    abstract val day: Int

    val date: String
        get() = getDateString(year, month, day)
}

sealed class HourlyReport : Report(), AdapterItem {
    abstract val reportedHours: Double
}

data class RegularHourlyReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        override val reportedHours: Double,
        val project: Project,
        val description: String) : HourlyReport()

data class PaidVacationHourlyReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        override val reportedHours: Double) : HourlyReport()

data class DailyReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        val reportType: DailyReportType) : Report()

enum class DailyReportType(val description: String) {
    UNPAID_VACATIONS("UnpaidVacation"),
    SICK_LEAVE("SickLeave"),
    PAID_CONFERENCE("PaidConference"),
}
