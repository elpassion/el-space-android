package pl.elpassion.report

import pl.elpassion.project.Project
import java.io.Serializable

data class HoursReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        val reportedHours: Double,
        val project: Project?,
        val description: String,
        val reportType: HoursReportType) : Serializable, Report

data class DayReport(
        override val id: Long,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        val reportType: DayReportType) : Serializable, Report

interface Report {
    val id: Long
    val year: Int
    val month: Int
    val day: Int
}

enum class HoursReportType {
    REGULAR,
    PAID_VACATIONS,
}

enum class DayReportType {
    UNPAID_VACATIONS,
    SICK_LEAVE
}