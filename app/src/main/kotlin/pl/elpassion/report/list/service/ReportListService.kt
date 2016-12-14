package pl.elpassion.report.list.service

import pl.elpassion.project.CachedProjectRepository
import pl.elpassion.report.Report
import pl.elpassion.report.list.ReportList
import rx.Observable

class ReportListService(val reportApi: ReportList.ReportApi, val projectApi: ReportList.ProjectApi, val repository: CachedProjectRepository) : ReportList.Service {
    override fun getReports(): Observable<List<Report>> {
        return projectApi.getProjects()
                .map { it.distinct() }
                .doOnNext { repository.saveProjects(it) }
                .flatMap { projects ->
                    reportApi.getReports().map { reportList ->
                        reportList.map { reportFromApi -> reportFromApi.toReport(projects) }
                    }
                }
    }
}