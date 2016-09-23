package pl.elpassion.report.add

import pl.elpassion.project.common.Project

interface ReportAdd {
    interface View {
        fun showSelectedProject(project: Project)

        fun openProjectChooser()
    }
}