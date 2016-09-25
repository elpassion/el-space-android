package pl.elpassion.report.list

import pl.elpassion.project.ProjectRepository
import rx.Observable

class ReportListService(val reportApi: ReportList.ReportApi, val projectApi: ReportList.ProjectApi, val repository: ProjectRepository) : ReportList.Service {
    override fun getReports(): Observable<List<Report>> {
        return projectApi.getProjects()
                .doOnNext {
                    repository.saveProjects(it)
                }
                .flatMap {
                    projects ->
                    reportApi.getReports().map {
                        it.map {
                            it.toReport(projects)
                        }
                    }
                }
    }
}