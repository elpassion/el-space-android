package pl.elpassion.report.list.service

import pl.elpassion.project.Project
import pl.elpassion.report.Report
import pl.elpassion.report.ReportType

class ReportFromApi(val id: Long, val performedAt: String, val value: Double?, val projectId: Long?, val comment: String?, val reportType: Int) {

    fun toReport(projects: List<Project>): Report {
        val date = performedAt.split("-")
        return Report(
                id = id,
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportedHours = value ?: 0.0,
                projectId = projectId ?: -1,
                projectName = projects.firstOrNull { it.id == projectId }?.name ?: "Unknown",
                description = comment ?: "",
                reportType = mapReportType(reportType))
    }

    private fun mapReportType(reportType: Int): ReportType {
        return when (reportType) {
            0 -> ReportType.REGULAR
            1 -> ReportType.PAID_VACATIONS
            2 -> ReportType.UNPAID_VACATIONS
            3 -> ReportType.SICK_LEAVE
            else -> ReportType.UNKNOWN
        }
    }
}