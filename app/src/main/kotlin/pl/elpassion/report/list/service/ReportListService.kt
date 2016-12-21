package pl.elpassion.report.list.service

import pl.elpassion.report.Report
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
}