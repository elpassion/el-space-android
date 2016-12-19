package pl.elpassion.project

import pl.elpassion.report.list.ReportList
import pl.elpassion.report.list.service.ProjectListService
import rx.Observable


class ProjectRepositoryImpl(private val projectListService: ProjectListService,
                            private val cachedProjectRepository: CachedProjectRepository) : ProjectRepository {
    override fun getProjects(): Observable<List<Project>> {
        if (cachedProjectRepository.hasProjects()) {
            return Observable.just(cachedProjectRepository.getPossibleProjects())
        } else {
            return projectListService.getProjects()
        }
    }
}
