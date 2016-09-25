package pl.elpassion.project

interface ProjectRepository {
    fun getPossibleProjects(): List<Project>
    fun saveProjects(projects: List<Project>)
}
