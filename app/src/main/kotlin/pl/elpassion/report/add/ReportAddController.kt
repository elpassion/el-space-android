package pl.elpassion.report.add

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getDateString
import pl.elpassion.common.extensions.getTimeFrom
import rx.Completable
import rx.Subscription
import java.util.Calendar.*

class ReportAddController(date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api) {

    private var selectedDate: String = date ?: getCurrentDatePerformedAtString()
    private var subscription: Subscription? = null

    fun onCreate() {
        view.showDate(selectedDate)
    }

    private fun getCurrentDatePerformedAtString(): String {
        val currentCalendar = getTimeFrom(timeInMillis = CurrentTimeProvider.get())
        return getDateString(currentCalendar.get(YEAR), currentCalendar.get(MONTH) + 1, currentCalendar.get(DAY_OF_MONTH))
    }

    fun onReportAdd() {
        view.showLoader()
        view.close()
    }

    private fun callApi(apiCall: Completable) {
        subscription = apiCall
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
        when (reportType) {
            ReportType.REGULAR -> view.showRegularReportDetails()
            ReportType.PAID_VACATIONS -> view.showPaidVacationsReportDetails()
            ReportType.SICK_LEAVE -> view.showSickLeaveReportDetails()
            ReportType.UNPAID_VACATIONS -> view.showUnpaidVacationsReportDetails()
        }
    }

    fun onReportTypeSwitch(reportType: ReportType) {

    }
}