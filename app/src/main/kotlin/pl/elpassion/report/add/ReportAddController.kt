package pl.elpassion.report.add

import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getDateString
import pl.elpassion.common.extensions.getTimeFrom
import rx.Observable
import rx.Subscription
import java.util.Calendar.*

class ReportAddController(date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api) {

    private var selectedDate: String = date ?: getCurrentDatePerformedAtString()
    private var subscription: Subscription? = null

    fun onCreate() {
        view.showDate(selectedDate)
        subscription = view.addReportClicks()
                .switchMap {
                    api.addRegularReport(selectedDate, 1, "8", "description")
                            .doOnSubscribe { view.showLoader() }
                            .doOnTerminate { view.hideLoader() }
                            .doOnUnsubscribe { view.hideLoader() }
                            .doOnCompleted { view.close() }
                            .toObservable<Unit>()
                }
                .doOnError { view.showError(it) }
                .onErrorResumeNext { Observable.empty() }
                .subscribe()
    }

    private fun getCurrentDatePerformedAtString(): String {
        val currentCalendar = getTimeFrom(timeInMillis = CurrentTimeProvider.get())
        return getDateString(currentCalendar.get(YEAR), currentCalendar.get(MONTH) + 1, currentCalendar.get(DAY_OF_MONTH))
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    fun onDateSelect(performedDate: String) {
        selectedDate = performedDate
        view.showDate(performedDate)
    }

    fun onReportTypeChanged(reportType: ReportType) {
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
}