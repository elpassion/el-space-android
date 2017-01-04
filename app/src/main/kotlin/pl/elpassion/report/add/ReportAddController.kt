package pl.elpassion.report.add

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getDateString
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.report.add.details.ReportAddDetails
import rx.Completable
import rx.Subscription
import java.util.Calendar.*

class ReportAddController(date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api) :
        ReportAddDetails.Sender.Regular,
        ReportAddDetails.Sender.PaidVacations,
        ReportAddDetails.Sender.UnpaidVacations,
        ReportAddDetails.Sender.SickLeave {

    private var selectedDate: String = date ?: getCurrentDatePerformedAtString()
    private var subscription: Subscription? = null

    fun onCreate() {
        view.showDate(selectedDate)
    }

    private fun getCurrentDatePerformedAtString(): String {
        val currentCalendar = getTimeFrom(timeInMillis = CurrentTimeProvider.get())
        return getDateString(currentCalendar.get(YEAR), currentCalendar.get(MONTH) + 1, currentCalendar.get(DAY_OF_MONTH))
    }

    fun onReportAdd(detailsController: ReportAddDetails.Controller) {
        detailsController.onReportAdded()
    }

    override fun addRegularReport(description: String, hours: String, projectId: Long) {
        callApi(apiCall = api.addRegularReport(selectedDate, projectId, hours, description))
    }

    override fun addPaidVacationsReport(hours: String) {
        callApi(apiCall = api.addPaidVacationsReport(selectedDate, hours))
    }

    override fun addUnpaidVacationsReport() {
        callApi(apiCall = api.addUnpaidVacationsReport(selectedDate))
    }

    override fun addSickLeaveReport() {
        callApi(apiCall = api.addSickLeaveReport(selectedDate))
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
}