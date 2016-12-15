package pl.elpassion.project

import pl.elpassion.report.list.ReportList
import rx.Observable


class ProjectRepositoryImpl(private val projectApi: ReportList.ProjectApi,
                            private val cachedProjectRepository: CachedProjectRepository) : ProjectRepository {
    override fun getProjects(): Observable<List<Project>> {
        if (cachedProjectRepository.hasProjects()) {
            return Observable.just(cachedProjectRepository.getPossibleProjects())
        } else {
            return projectApi.getProjects()
        }
    }
}
