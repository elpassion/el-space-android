package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.hub.report.*
import pl.elpassion.elspace.hub.report.add.ReportType
import rx.subscriptions.CompositeSubscription

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        view.showDate(report.date)
        view.reportTypeChanges()
                .startWith(report.type)
                .doOnNext { onReportTypeChanged(it) }
                .subscribe()
                .addTo(subscriptions)
    }

    private fun onReportTypeChanged(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> showRegularForm()
        ReportType.PAID_VACATIONS -> showPaidVacationsForm()
        ReportType.SICK_LEAVE -> showSickLeaveForm()
        ReportType.UNPAID_VACATIONS -> showUnpaidVacationsForm()
    }

    private fun showRegularForm() {
        view.showRegularForm()
    }

    private fun showPaidVacationsForm() {
        view.showPaidVacationsForm()
    }

    private fun showSickLeaveForm() {
        view.showSickLeaveForm()
    }

    private fun showUnpaidVacationsForm() {
        view.showUnpaidVacationsForm()
    }
}

private val Report.type: ReportType
    get() = when (this) {
        is RegularHourlyReport -> ReportType.REGULAR
        is PaidVacationHourlyReport -> ReportType.PAID_VACATIONS
        is DailyReport -> when (reportType) {
            DailyReportType.SICK_LEAVE -> ReportType.SICK_LEAVE
            DailyReportType.UNPAID_VACATIONS -> ReportType.UNPAID_VACATIONS
        }
    }