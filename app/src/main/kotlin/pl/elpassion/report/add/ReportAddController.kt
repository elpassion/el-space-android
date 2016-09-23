package pl.elpassion.report.add

import android.util.Log
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository

class ReportAddController(val view: ReportAdd.View, val repository: ProjectRepository, val api: ReportAdd.Api) {

    lateinit var date: String
    lateinit var project: Project

    fun onCreate(date: String) {
        this.date = date
        view.showDate(date)
        onSelectProject(repository.getPossibleProjects().first())
    }

    fun onProjectClicked() {
        view.openProjectChooser()
    }

    fun onSelectProject(project: Project) {
        this.project = project
        view.showSelectedProject(project)
    }

    fun onReportAdd(hours: String, description: String) {
        api.addReport(date, project.id, hours, description).subscribe({
            view.close()
        }, {
            Log.e("Eroor", it.toString(), it)
            view.showError()
        })
    }
}