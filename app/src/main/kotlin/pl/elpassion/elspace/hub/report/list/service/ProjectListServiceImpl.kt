package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.project.CachedProjectRepository
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.list.ReportList
import rx.Observable

class ProjectListServiceImpl(val projectApi: ReportList.ProjectApi,
                             val repository: CachedProjectRepository) : ProjectListService {

    override fun getProjects(): Observable<List<Project>> {
        return projectApi.getProjects()
                .map { it.distinct() }
                .doOnNext { repository.saveProjects(it) }
    }
}