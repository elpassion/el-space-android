package pl.elpassion.report.add

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.project.Project
import pl.elpassion.project.last.LastSelectedProjectRepository
import rx.Subscription
import java.util.Calendar.*

class ReportAddController(date: String?,
                          private val view: ReportAdd.View,
                          private val repository: LastSelectedProjectRepository,
                          private val api: ReportAdd.Api) {
    private var selectedDate: String = date ?: getCurrentDatePerformedAtString()
    private var project: Project? = null
    private var subscription: Subscription? = null

    fun onCreate() {
        view.showDate(selectedDate)
        repository.getLastProject()?.let { onSelectProject(it) }
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
        view.enableAddReportButton()
    }

    fun onReportAdd(hours: String, description: String) {
        if (description.isEmpty()) {
            view.showEmptyDescriptionError()
        } else {
            sendAddReport(description, hours)
        }
    }

    private fun sendAddReport(description: String, hours: String) {
        subscription = api.addReport(selectedDate, project!!.id, hours, description)
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    fun onDateSelect(performedDate: String) {
        selectedDate = performedDate
        view.showDate(performedDate)
    }

    fun onReportTypeChanged(reportType: ReportType) {
        if (reportType in listOf(ReportType.REGULAR, ReportType.PAID_VACATIONS)) {
            view.showHoursInput()
            view.showRegularReportDetails()
        } else {
            view.hideHoursInput()
        }
    }
}