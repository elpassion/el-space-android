package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.*
import rx.Completable
import rx.Observable
import rx.subscriptions.CompositeSubscription

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View,
                           private val api: ReportEdit.Api,
                           private val schedulers: SchedulersSupplier) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        showReport()
        editReportClicks()
                .subscribe()
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

    private fun editReportClicks() = view.editReportClicks()
            .withLatestFrom(reportTypeChanges(), { model, handler -> model to handler })
            .switchMap { callApi(it).toSingleDefault(Unit).toObservable() }
            .doOnNext { view.close() }
            .onErrorResumeNext {
                view.showError()
                Observable.empty()
            }

    private fun reportTypeChanges() = view.reportTypeChanges()
            .startWith(report.type)
            .doOnNext { onReportTypeChanged(it) }
            .map { chooseReportEditHandler(it) }

    private fun chooseReportEditHandler(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> regularReportEditHandler
        ReportType.PAID_VACATIONS -> paidVacationReportEditHandler
        ReportType.UNPAID_VACATIONS -> unpaidVacationReportEditHandler
        ReportType.SICK_LEAVE -> sickLeaveReportEditHandler
    }

    private fun callApi(modelCallPair: Pair<ReportViewModel, (ReportViewModel) -> Completable>) =
            modelCallPair.second(modelCallPair.first)
                    .subscribeOn(schedulers.subscribeOn)
                    .observeOn(schedulers.observeOn)
                    .addLoader()

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

    private val regularReportEditHandler = { model: ReportViewModel ->
        (model as RegularReport).let {
            api.editReport(report.id, ReportType.REGULAR.id, model.selectedDate, model.hours, model.description, model.project?.id)
        }
    }

    private val paidVacationReportEditHandler = { model: ReportViewModel ->
        (model as PaidVacationsReport).let {
            api.editReport(report.id, ReportType.PAID_VACATIONS.id, model.selectedDate, model.hours, null, null)
        }
    }

    private val unpaidVacationReportEditHandler = { model: ReportViewModel ->
        api.editReport(report.id, ReportType.UNPAID_VACATIONS.id, model.selectedDate, null, null, null)
    }

    private val sickLeaveReportEditHandler = { model: ReportViewModel ->
        api.editReport(report.id, ReportType.SICK_LEAVE.id, model.selectedDate, null, null, null)
    }

    private fun Completable.addLoader() = this
            .doOnSubscribe { view.showLoader() }
            .doOnCompleted { view.hideLoader() }

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