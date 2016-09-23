package pl.elpassion.project.common

interface ProjectRepository {
    fun getPossibleProjects(): List<Project>
}