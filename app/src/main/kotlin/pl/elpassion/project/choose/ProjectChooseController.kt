package pl.elpassion.project.choose

import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository

class ProjectChooseController(val view: ProjectChoose.View, val repository: ProjectRepository) {
    private lateinit var sortedProjectsList: List<Project>

    fun onCreate() {
        sortedProjectsList = repository.getPossibleProjects().sortedBy { it.name }
        view.showPossibleProjects(sortedProjectsList)
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }

    fun searchQuery(query: String) {
        view.showFiltredProjects(filterProjectByQuery(query))
    }

    private fun filterProjectByQuery(query: String) = sortedProjectsList.filter { it.name.contains(query, true) }
}