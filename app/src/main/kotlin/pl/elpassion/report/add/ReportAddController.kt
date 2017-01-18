package pl.elpassion.report.add

import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getDateString
import pl.elpassion.common.extensions.getTimeFrom
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.util.Calendar.*

class ReportAddController(private val date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        view.showDate(date ?: getCurrentDatePerformedAtString())
        view.addReportClicks()
                .switchMap { handleNewReport(it) }
                .doOnError { view.showError(it) }
                .onErrorResumeNext { Observable.empty() }
                .subscribe()
                .save()
        view.reportTypeChanges()
                .subscribe({ onReportTypeChanged(it) })
                .save()
    }

    private fun handleNewReport(report: ReportViewModel): Observable<Unit>? {
        return dispatchReportToApi(report)
                .doOnSubscribe { view.showLoader() }
                .doOnTerminate { view.hideLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .doOnCompleted { view.close() }
                .toObservable<Unit>()
    }

    private fun dispatchReportToApi(report: ReportViewModel) = when (report) {
        is RegularReport -> api.addRegularReport(report.selectedDate, 1, "8", "description")
        is PaidVacationsReport -> api.addPaidVacationsReport(report.selectedDate, "8")
        is SickLeaveReport -> api.addSickLeaveReport(report.selectedDate)
        is UnpaidVacationsReport -> api.addUnpaidVacationsReport(report.selectedDate)
        else -> throw IllegalArgumentException(report.toString())
    }


    private fun getCurrentDatePerformedAtString(): String {
        val currentCalendar = getTimeFrom(timeInMillis = CurrentTimeProvider.get())
        return getDateString(currentCalendar.get(YEAR), currentCalendar.get(MONTH) + 1, currentCalendar.get(DAY_OF_MONTH))
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    fun onDateSelect(performedDate: String) {
        view.showDate(performedDate)
    }

    private fun onReportTypeChanged(reportType: ReportType) {
        when (reportType) {
            ReportType.REGULAR -> showRegularForm()
            ReportType.PAID_VACATIONS -> showPaidVacationsForm()
            ReportType.SICK_LEAVE -> showSickLeaveForm()
            ReportType.UNPAID_VACATIONS -> showUnpaidVacationsForm()
        }
    }

    private fun showUnpaidVacationsForm() {
        view.hideHoursInput()
        view.hideDescriptionInput()
        view.hideProjectChooser()
    }

    private fun showSickLeaveForm() {
        view.hideHoursInput()
        view.hideDescriptionInput()
        view.hideProjectChooser()
    }

    private fun showPaidVacationsForm() {
        view.showHoursInput()
        view.hideProjectChooser()
        view.hideDescriptionInput()
    }

    private fun showRegularForm() {
        view.showDescriptionInput()
        view.showProjectChooser()
        view.showHoursInput()
    }

    private fun Subscription.save() {
        subscriptions.add(this)
    }
}
