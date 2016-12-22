package pl.elpassion.report.list.service

import pl.elpassion.project.Project
import pl.elpassion.report.*
import pl.elpassion.report.list.ReportList
import rx.Observable

class ReportListService(private val reportApi: ReportList.ReportApi,
                        private val projectApi: ProjectListService) : ReportList.Service {

    override fun getReports(): Observable<List<Report>> = projectApi.getProjects()
            .flatMap { projects ->
                reportApi.getReports().map { reportList ->
                    reportList.map { reportFromApi -> reportFromApi.toReport(projects) }
                }
            }

    fun ReportFromApi.toReport(projects: List<Project>): Report {
        return when (reportType) {
            0, 1 -> toHoursReport(projects)
            else -> toDayReport()
        }
    }

    private fun ReportFromApi.toDayReport(): DailyReport {
        val date = performedAt.split("-")
        return DailyReport(
                id = id,
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportType = reportType.toDayReportType())
    }

    private fun ReportFromApi.toHoursReport(projects: List<Project>): HourlyReport {
        val date = performedAt.split("-")
        return HourlyReport(
                id = id,
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportedHours = value ?: 0.0,
                project = projects.firstOrNull { it.id == projectId },
                description = comment ?: "",
                reportType = reportType.toHoursReportType())
    }

    private fun Int.toHoursReportType(): HourlyReportType {
        return when(this) {
            0 -> HourlyReportType.REGULAR
            else -> HourlyReportType.PAID_VACATIONS
        }
    }

    private fun Int.toDayReportType(): DailyReportType {
        return when(this) {
            3 -> DailyReportType.SICK_LEAVE
            else -> DailyReportType.UNPAID_VACATIONS
        }
    }
}
