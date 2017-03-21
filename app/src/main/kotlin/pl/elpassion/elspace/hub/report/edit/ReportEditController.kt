package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.*
import pl.elpassion.elspace.hub.report.ReportType
import rx.subscriptions.CompositeSubscription

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View,
                           private val api: ReportEdit.Api) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        showReport()
        view.reportTypeChanges()
                .startWith(report.type)
                .subscribe { onReportTypeChanged(it) }
                .addTo(subscriptions)
        view.editReportClicks()
                .subscribe { editReport(it) }
                .addTo(subscriptions)
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    fun onDateChanged(date: String) {
        view.showDate(date)
    }

    fun onProjectChanged(project: Project) {
        view.showProjectName(project.name)
    }

    private fun showReport() {
        view.showReportType(report.type)
        view.showDate(report.date)
        if (report is HourlyReport) {
            showHourlyReport(report)
        }
    }

    private fun showHourlyReport(report: HourlyReport) {
        view.showReportedHours(report.reportedHours)
        if (report is RegularHourlyReport) {
            view.showProjectName(report.project.name)
            view.showDescription(report.description)
        }
    }

    private fun onReportTypeChanged(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> showRegularForm()
        ReportType.PAID_VACATIONS -> showPaidVacationsForm()
        ReportType.SICK_LEAVE -> showSickLeaveForm()
        ReportType.UNPAID_VACATIONS -> showUnpaidVacationsForm()
    }

    private fun editReport(model: ReportViewModel) {
        when (model) {
            is RegularReport -> api.editReport(report.id, ReportType.REGULAR.id, model.selectedDate, model.hours, model.description, model.project?.id)
            is PaidVacationsReport -> api.editReport(report.id, ReportType.PAID_VACATIONS.id, model.selectedDate, model.hours, null, null)
            is UnpaidVacationsReport -> api.editReport(report.id, ReportType.UNPAID_VACATIONS.id, model.selectedDate, null, null, null)
            is SickLeaveReport -> api.editReport(report.id, ReportType.SICK_LEAVE.id, model.selectedDate, null, null, null)
        }
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