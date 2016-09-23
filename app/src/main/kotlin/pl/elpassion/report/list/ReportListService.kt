package pl.elpassion.report.list

import rx.Observable

class ReportListService(val reportApi: ReportList.ReportApi, val projectApi: ReportList.ProjectApi) : ReportList.Service {
    override fun getReports(): Observable<List<Report>> {
        return projectApi.getProjects().flatMap { projects -> reportApi.getReports().map { it.map { it.toReport(projects) } } }
    }
}