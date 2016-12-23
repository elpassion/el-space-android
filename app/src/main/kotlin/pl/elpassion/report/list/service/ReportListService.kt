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
            0 -> toRegularHourlyReport(projects)
            1-> toPaidVacationHourlyReport()
            else -> toDayReport()
        }
    }

    private fun ReportFromApi.toPaidVacationHourlyReport(): Report {
        val date = performedAt.split("-")
        return PaidVacationHourlyReport(
                id = id,
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportedHours = value ?: 0.0)
    }

    private fun ReportFromApi.toDayReport(): DailyReport {
        val date = performedAt.split("-")
        return DailyReport(
                id = id,
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportType = reportType.toDailyReportType())
    }

    private fun ReportFromApi.toRegularHourlyReport(projects: List<Project>): HourlyReport {
        val date = performedAt.split("-")
        return RegularHourlyReport(
                id = id,
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportedHours = value ?: 0.0,
                project = projects.first { it.id == projectId },
                description = comment ?: "")
    }

    private fun Int.toDailyReportType(): DailyReportType {
        return when(this) {
            3 -> DailyReportType.SICK_LEAVE
            else -> DailyReportType.UNPAID_VACATIONS
        }
    }
}
