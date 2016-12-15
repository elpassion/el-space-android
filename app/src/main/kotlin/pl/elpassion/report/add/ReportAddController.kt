package pl.elpassion.report.add

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import java.util.Calendar.*

class ReportAddController(private val view: ReportAdd.View, private val repository: ProjectRepository, private val api: ReportAdd.Api) {

    lateinit var date: String
    lateinit var project: Project

    fun onCreate(selectedDate: String?) {
        date = selectedDate ?: getCurrentDatePerformedAtString()
        view.showDate(date)
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
        api.addReport(date, project.id, hours, description)
                .applySchedulers()
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }
}