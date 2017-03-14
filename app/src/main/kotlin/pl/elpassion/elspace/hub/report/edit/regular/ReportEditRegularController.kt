package pl.elpassion.elspace.hub.report.edit.regular

import pl.elpassion.elspace.api.applySchedulers
import pl.elpassion.elspace.common.extensions.dayOfMonth
import pl.elpassion.elspace.common.extensions.month
import pl.elpassion.elspace.common.extensions.toCalendarDate
import pl.elpassion.elspace.common.extensions.year
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import rx.Subscription
import kotlin.properties.Delegates

class ReportEditRegularController(private val view: ReportEdit.Regular.View,
                                  private val editReportService: ReportEdit.Regular.Service,
                                  private val removeReportApi: ReportEdit.RemoveApi) {

    private var report: RegularHourlyReport by Delegates.notNull()
    private var subscription: Subscription? = null
    private var removeReportSubscription: Subscription? = null

    fun onCreate(report: RegularHourlyReport) {
        this.report = report
        view.showReport(report)
        view.showDate(report.date)
    }

    fun onChooseProject() {
        view.openChooseProjectScreen()
    }

    fun onSaveReport(hours: String, description: String) {
        if (description.isEmpty()) {
            view.showEmptyDescriptionError()
        } else {
            sendEditedReport(description, hours)
        }
    }

    private fun sendEditedReport(description: String, hours: String) {
        subscription = editReportService.edit(report.copy(description = description, reportedHours = hours.toDouble()))
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }

    fun onSelectProject(project: Project) {
        report = report.copy(project = project)
        view.updateProjectName(project.name)
    }

    fun onRemoveReport() {
        removeReportSubscription = removeReportApi.removeReport(report.id)
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
        removeReportSubscription?.unsubscribe()
    }

    fun onDateSelect(performedDate: String) {
        val calendar = performedDate.toCalendarDate()
        report = report.copy(day = calendar.dayOfMonth, month = calendar.month + 1, year = calendar.year)
        view.showDate(performedDate)
    }
}