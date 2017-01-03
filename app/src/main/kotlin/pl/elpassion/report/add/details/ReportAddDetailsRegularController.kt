package pl.elpassion.report.add.details

import pl.elpassion.project.Project
import pl.elpassion.project.last.LastSelectedProjectRepository

class ReportAddDetailsRegularController(private val view: ReportAddDetails.View.Regular,
                                        private val sender: ReportAddDetails.Sender.Regular,
                                        private val repository: LastSelectedProjectRepository) : ReportAddDetails.Controller {

    private var selectedProject: Project? = null

    fun onCreate() {
        repository.getLastProject()?.let {
            view.showSelectedProject(it)
            selectedProject = it
        }
    }

    fun onProjectClicked() {
        view.openProjectChooser()
    }

    fun onSelectProject(newProject: Project) {
        view.showSelectedProject(newProject)
        selectedProject = newProject
    }

    override fun onReportAdded() {
        if (view.getDescription().isBlank()) {
            view.showEmptyDescriptionError()
        } else if(selectedProject == null){
            view.showEmptyProjectError()
        }else {
            sender.sendAddReport(view.getDescription(), view.getHours())
        }
    }
}