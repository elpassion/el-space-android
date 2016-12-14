package pl.elpassion.project

interface CachedProjectRepository {
    fun getPossibleProjects(): List<Project>
    fun saveProjects(projects: List<Project>)
    fun hasProjects(): Boolean
}
