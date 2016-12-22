package pl.elpassion.report

import java.io.Serializable

data class Report(
        val id : Long,
        val year: Int,
        val month: Int,
        val day: Int,
        val reportedHours: Double,
        val projectName: String,
        val projectId: Long,
        val description: String,
        val reportType: ReportType) : Serializable

enum class ReportType {
    REGULAR,
    UNPAID_VACATIONS,
    PAID_VACATIONS,
    SICK_LEAVE,
    UNKNOWN
}