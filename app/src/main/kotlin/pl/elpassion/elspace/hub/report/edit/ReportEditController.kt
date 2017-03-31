package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.common.extensions.catchOnError
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.*
import rx.Observable
import rx.subscriptions.CompositeSubscription

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View,
                           private val api: ReportEdit.Api,
                           private val schedulers: SchedulersSupplier) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        showReport()
        Observable.merge(editReportClicks(), removeReportClicks())
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
        view.showProject(project)
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
            .switchMap { callApiToEdit(it) }
            .doOnNext { view.close() }
            .catchOnError { view.showError(it) }

    private fun removeReportClicks() = view.removeReportClicks()
            .switchMap { callApiToRemove(report.id) }
            .doOnNext { view.close() }
            .catchOnError { view.showError(it) }

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

    private fun callApiToEdit(modelCallPair: Pair<ReportViewModel, (ReportViewModel) -> Observable<Unit>>) =
            modelCallPair.second(modelCallPair.first).async()

    private fun callApiToRemove(reportId: Long) =
            api.removeReport(reportId).map { Unit }.async()

    private fun Observable<Unit>.async() = this
            .subscribeOn(schedulers.subscribeOn)
            .observeOn(schedulers.observeOn)
            .addLoader()

    private fun showHourlyReport(report: HourlyReport) {
        view.showReportedHours(report.reportedHours)
        if (report is RegularHourlyReport) {
            view.showProject(report.project)
            view.showDescription(report.description)
        }
    }

    private fun onReportTypeChanged(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> view.showRegularForm()
        ReportType.PAID_VACATIONS -> view.showPaidVacationsForm()
        ReportType.SICK_LEAVE -> view.showSickLeaveForm()
        ReportType.UNPAID_VACATIONS -> view.showUnpaidVacationsForm()
    }

    private val regularReportEditHandler = { model: ReportViewModel ->
        (model as RegularReport).run {
            when {
                project == null -> {
                    view.showEmptyProjectError()
                    Observable.empty()
                }
                description.isBlank() -> {
                    view.showEmptyDescriptionError()
                    Observable.empty()
                }
                else -> editRegularReport(this, project)
            }
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

    private fun editRegularReport(model: RegularReport, project: Project) =
            api.editReport(report.id, ReportType.REGULAR.id, model.selectedDate, model.hours, model.description, project.id)

    private fun Observable<Unit>.addLoader() = this
            .doOnSubscribe { view.showLoader() }
            .doOnUnsubscribe { view.hideLoader() }
            .doOnTerminate { view.hideLoader() }
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