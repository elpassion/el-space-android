package pl.elpassion.project.choose

import pl.elpassion.project.common.Project

interface ProjectChoose {
    interface View {
        fun showPossibleProjects(projects: List<Project>)

        fun selectProject(project: Project)
    }
}
