package pl.elpassion.project.choose

import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository

class ProjectChooseController(val view: ProjectChoose.View, val repository: ProjectRepository) {
    fun onCreate() {
        view.showPossibleProjects(repository.getPossibleProjects().sortedBy { it.name })
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }
}