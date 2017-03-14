package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.*
import pl.elpassion.elspace.hub.report.list.ReportList
import pl.elpassion.elspace.hub.report.list.YearMonth
import pl.elpassion.elspace.hub.report.list.toMonthDateRange
import rx.Observable

class ReportListService(
        private val reportApi: ReportList.ReportApi,
        private val projectService: ProjectListService) : ReportList.Service {

    override fun getReports(yearMonth: YearMonth): Observable<List<Report>> = projectService.getProjects()
            .flatMap { projects ->
                val (startOfMonth, endOfMonth) = yearMonth.toMonthDateRange()
                reportApi.getReports(startOfMonth, endOfMonth).map { reportList ->
                    reportList.map { reportFromApi -> reportFromApi.toReport(projects) }
                }
            }

    fun ReportFromApi.toReport(projects: List<Project>) = when (reportType) {
        0 -> toRegularHourlyReport(projects)
        1 -> toPaidVacationHourlyReport()
        else -> toDayReport()
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

    private fun ReportFromApi.toDayReport(): DailyReport {
        val date = performedAt.split("-")
        return DailyReport(
                id = id,
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportType = reportType.toDailyReportType())
    }

    private fun Int.toDailyReportType() = when (this) {
        3 -> DailyReportType.SICK_LEAVE
        else -> DailyReportType.UNPAID_VACATIONS
    }
}
