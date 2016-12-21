package pl.elpassion.project

import pl.elpassion.report.list.service.ProjectListService
import rx.Observable


class ProjectRepositoryImpl(private val projectListService: ProjectListService,
                            private val cachedProjectRepository: CachedProjectRepository) : ProjectRepository {

    override fun getProjects(): Observable<List<Project>> = when {
        cachedProjectRepository.hasProjects() -> Observable.just(cachedProjectRepository.getPossibleProjects())
        else -> projectListService.getProjects()
    }
}
