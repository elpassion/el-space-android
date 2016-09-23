package pl.elpassion.report.add

import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository

class ReportAddController(val view: ReportAdd.View, val api: ProjectRepository) {

    fun onCreate() {
        view.showSelectedProject(api.getPossibleProjects().first())
    }

    fun onProjectClicked() {
        view.openProjectChooser()
    }

    fun onSelectProject(project: Project) {
        view.showSelectedProject(project)
    }

    fun onReportAdd(hours: String, description: String) {
        view.close()
    }
}