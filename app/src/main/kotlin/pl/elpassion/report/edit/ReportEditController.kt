package pl.elpassion.report.edit

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.extensions.*
import pl.elpassion.project.Project
import pl.elpassion.report.RegularHourlyReport
import rx.Subscription
import kotlin.properties.Delegates

class ReportEditController(private val view: ReportEdit.View,
                           private val editReportApi: ReportEdit.Regular.Service,
                           private val removeReportApi: ReportEdit.RemoveApi) {

    private var report: RegularHourlyReport by Delegates.notNull()
    private var subscription: Subscription? = null
    private var removeReportSubscription: Subscription? = null

    fun onCreate(report: RegularHourlyReport) {
        this.report = report
        val performedDate = getPerformedAtString(report.year, report.month, report.day)
        view.showDate(performedDate)
    }

    fun onSaveReport(hours: String) {
        sendEditedReport(hours)
    }

    private fun sendEditedReport(hours: String, description: String = "") {
        subscription = editReportApi.edit(report.copy(description = description, reportedHours = hours.toDouble()))
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
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