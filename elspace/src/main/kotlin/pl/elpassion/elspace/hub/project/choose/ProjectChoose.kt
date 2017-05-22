package pl.elpassion.elspace.hub.project.choose

import pl.elpassion.elspace.hub.project.Project

interface ProjectChoose {
    interface View {
        fun showProjects(projects: List<Project>)

        fun selectProject(project: Project)

        fun showError(ex: Throwable)

        fun hideLoader()

        fun showLoader()
    }
}
