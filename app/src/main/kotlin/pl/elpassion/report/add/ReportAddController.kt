package pl.elpassion.report.add

import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository

class ReportAddController(val view: ReportAdd.View, val repository: ProjectRepository, val api: ReportAdd.Api) {

    fun onCreate() {
        view.showSelectedProject(repository.getPossibleProjects().first())
    }

    fun onProjectClicked() {
        view.openProjectChooser()
    }

    fun onSelectProject(project: Project) {
        view.showSelectedProject(project)
    }

    fun onReportAdd(hours: String, description: String) {
        api.addReport().subscribe({
            view.close()
        }, {
            view.showError()
        })
    }
}