package pl.elpassion.report.add.details.regular

import pl.elpassion.project.Project
import pl.elpassion.project.last.LastSelectedProjectRepository
import pl.elpassion.report.add.details.ReportAddDetails

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
            sender.addRegularReport(view.getDescription(), view.getHours())
        }
    }
}