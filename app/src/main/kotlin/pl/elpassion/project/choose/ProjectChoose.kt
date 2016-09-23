package pl.elpassion.project.choose

import pl.elpassion.common.Provider
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository

interface ProjectChoose {
    interface View {
        fun showPossibleProjects(projects: List<Project>)

        fun selectProject(project: Project)
    }

}