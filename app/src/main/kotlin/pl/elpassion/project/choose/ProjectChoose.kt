package pl.elpassion.project.choose

import pl.elpassion.project.dto.Project

interface ProjectChoose {
    interface View {
        fun showPossibleProjects(projects: List<Project>)

        fun selectProject(project: Project)
    }

    interface Repository {
        fun getPossibleProjects(): List<Project>
    }
}