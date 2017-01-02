package pl.elpassion.report.add

import pl.elpassion.project.Project
import pl.elpassion.project.last.LastSelectedProjectRepository

class ReportAddDetailsRegularController(private val view: ReportAdd.View.Regular,
                                        repository: LastSelectedProjectRepository) : ReportAddDetails.Controller {

    init {
        repository.getLastProject()?.let {
            view.showSelectedProject(it)
        }
    }

    fun onProjectClicked() {
        view.openProjectChooser()
    }

    fun onSelectProject(newProject: Project) {
        view.showSelectedProject(newProject)
    }

    override fun isReportValid() = view.getDescription().isNotBlank()

    override fun onError() {
        view.showEmptyDescriptionError()
    }
}