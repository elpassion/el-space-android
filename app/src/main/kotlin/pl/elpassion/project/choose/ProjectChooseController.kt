package pl.elpassion.project.choose

import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository

class ProjectChooseController(val view: ProjectChoose.View, val repository: ProjectRepository) {
    fun onCreate() {
        view.showPossibleProjects(repository.getPossibleProjects().sortedBy { it.name })
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }
}