package pl.elpassion.report.add

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.project.Project
import pl.elpassion.project.CachedProjectRepository
import java.util.Calendar.*

class ReportAddController(date: String?,
                          private val view: ReportAdd.View,
                          private val repository: CachedProjectRepository,
                          private val api: ReportAdd.Api) {
    private val selectedDate: String = date ?: getCurrentDatePerformedAtString()
    private var project: Project? = null

    fun onCreate() {
        view.showDate(selectedDate)
        if (repository.hasProjects()) {
            onSelectProject(getLastProject())
        }
    }

    private fun getLastProject() = repository.getPossibleProjects().first()

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
        view.enableAddReportButton()
    }

    fun onReportAdd(hours: String, description: String) {
        api.addReport(selectedDate, project!!.id, hours, description)
                .applySchedulers()
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }
}