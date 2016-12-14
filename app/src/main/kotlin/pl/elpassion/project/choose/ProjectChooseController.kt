package pl.elpassion.project.choose

import pl.elpassion.project.Project
import pl.elpassion.project.CachedProjectRepository

class ProjectChooseController(val view: ProjectChoose.View, val repository: CachedProjectRepository) {
    private lateinit var sortedProjectsList: List<Project>

    fun onCreate() {
        sortedProjectsList = repository.getPossibleProjects().sortedBy { it.name }
        view.showPossibleProjects(sortedProjectsList)
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }

    fun searchQuery(query: String) {
        view.showFilteredProjects(filterProjectByQuery(query))
    }

    private fun filterProjectByQuery(query: String) = sortedProjectsList.filter { it.name.contains(query, true) }
}