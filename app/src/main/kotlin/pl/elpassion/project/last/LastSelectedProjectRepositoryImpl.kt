package pl.elpassion.project.last

import pl.elpassion.project.CachedProjectRepository

class LastSelectedProjectRepositoryImpl(private val cachedProjectRepository: CachedProjectRepository) : LastSelectedProjectRepository {
    override fun getLastProject() = cachedProjectRepository.getPossibleProjects().firstOrNull()
}
