package pl.elpassion.report.add

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import java.util.Calendar.*

class ReportAddController(val date: String?,
                          val view: ReportAdd.View,
                          val repository: ProjectRepository,
                          val api: ReportAdd.Api) {
    private val selectedDate: String = date ?: getCurrentDatePerformedAtString()
    private lateinit var project: Project

    fun onCreate() {
        view.showDate(selectedDate)
        onSelectProject(repository.getPossibleProjects().first())
    }

    private fun getCurrentDatePerformedAtString(): String {
        val currentCalendar = getTimeFrom(timeInMillis = CurrentTimeProvider.get())
        return getPerformedAtString(currentCalendar.get(YEAR), currentCalendar.get(MONTH) + 1, currentCalendar.get(DAY_OF_MONTH))
    }

    fun onProjectClicked() {
        view.openProjectChooser()
    }

    fun onSelectProject(project: Project) {
        this.project = project
        view.showSelectedProject(project)
    }

    fun onReportAdd(hours: String, description: String) {
        api.addReport(selectedDate, project.id, hours, description)
                .applySchedulers()
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }
}