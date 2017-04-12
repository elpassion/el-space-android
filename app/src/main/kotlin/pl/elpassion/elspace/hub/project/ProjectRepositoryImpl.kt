package pl.elpassion.elspace.hub.project

import pl.elpassion.elspace.hub.report.list.service.ProjectListService
import io.reactivex.Observable


class ProjectRepositoryImpl(private val projectListService: ProjectListService,
                            private val cachedProjectRepository: CachedProjectRepository) : ProjectRepository {

    override fun getProjects(): Observable<List<Project>> = when {
        cachedProjectRepository.hasProjects() -> Observable.just(cachedProjectRepository.getPossibleProjects())
        else -> projectListService.getProjects()
    }
}
