package pl.elpassion.report.edit.paidvacation

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.report.HourlyReport
import pl.elpassion.report.Report
import pl.elpassion.report.edit.ReportEdit
import rx.Subscription
import kotlin.properties.Delegates

class ReportEditPaidVacationController(private val view: ReportEdit.PaidVacation.View,
                                       private val api: ReportEdit.PaidVacation.Service,
                                       private val removeReportApi: ReportEdit.RemoveApi) {

    private var report: Report by Delegates.notNull()
    private var selectedDate: String by Delegates.notNull()
    private var subscription: Subscription? = null
    private var removeReportSubscription: Subscription? = null

    fun onCreate(report: Report) {
        this.report = report
        val performedDate = getPerformedAtString(report.year, report.month, report.day)
        onDateSelect(performedDate)
        if (report is HourlyReport) {
            view.showReportHours(report.reportedHours)
        } else {
            view.hideReportHours()
        }
    }

    fun onSaveReport(hours: String) {
        subscription = api.edit(report.id, selectedDate, hours.toDouble())
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
        selectedDate = performedDate
        view.showDate(performedDate)
    }
}