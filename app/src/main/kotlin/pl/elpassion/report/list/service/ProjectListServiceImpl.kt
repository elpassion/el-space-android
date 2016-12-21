package pl.elpassion.report.list.service

import pl.elpassion.project.CachedProjectRepository
import pl.elpassion.project.Project
import pl.elpassion.report.list.ReportList
import rx.Observable

class ProjectListServiceImpl(val projectApi: ReportList.ProjectApi,
                             val repository: CachedProjectRepository) : ProjectListService {

    override fun getProjects(): Observable<List<Project>> {
        return projectApi.getProjects()
                .map { it.distinct() }
                .doOnNext { repository.saveProjects(it) }
    }
}