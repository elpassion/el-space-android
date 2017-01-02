package pl.elpassion.report.add

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.project.Project
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
        return getPerformedAtString(currentCalendar.get(YEAR), currentCalendar.get(MONTH) + 1, currentCalendar.get(DAY_OF_MONTH))
    }

    fun onReportAdd(detailsController: ReportAddDetails.Controller) {
        if (detailsController.isReportValid()) {
            sendAddReport("description", "8")
        } else {
            detailsController.onError()
        }
    }

    private fun sendAddReport(description: String, hours: String) {
        subscription = api.addReport(selectedDate, 1, hours, description)
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
            ReportType.REGULAR -> {
                view.showHoursInput()
                view.showRegularReportDetails()
            }
            ReportType.PAID_VACATIONS -> {
                view.showHoursInput()
                view.showPaidVacationsReportDetails()
            }
            ReportType.SICK_LEAVE -> {
                view.hideHoursInput()
                view.showSickLeaveReportDetails()
            }
            ReportType.UNPAID_VACATIONS -> {
                view.hideHoursInput()
                view.showUnpaidVacationsReportDetails()
            }
        }
    }
}