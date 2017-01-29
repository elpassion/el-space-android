package pl.elpassion.elspace.hub.project.last

import pl.elpassion.elspace.hub.project.CachedProjectRepository

class LastSelectedProjectRepositoryImpl(private val cachedProjectRepository: CachedProjectRepository) : LastSelectedProjectRepository {
    override fun getLastProject() = cachedProjectRepository.getPossibleProjects().firstOrNull()
}
