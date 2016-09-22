package pl.elpassion.project.choose

import pl.elpassion.project.dto.Project

class ProjectChooseController(val view: ProjectChoose.View, val repository: ProjectChoose.Repository) {
    fun onCreate() {
        view.showPossibleProjects(repository.getPossibleProjects())
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }
}