package pl.elpassion.report.add

import pl.elpassion.project.common.ProjectRepository

class ReportAddController(val view: ReportAdd.View, val api: ProjectRepository) {
    fun onCreate() {
        view.showSelectedProject(api.getPossibleProjects().first())
    }
}