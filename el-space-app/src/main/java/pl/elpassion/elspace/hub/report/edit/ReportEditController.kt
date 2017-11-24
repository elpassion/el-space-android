package pl.elpassion.elspace.hub.report.edit

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.catchOnError
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.*

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View,
                           private val api: ReportEdit.Api,
                           private val schedulers: SchedulersSupplier) {

    private val subscriptions = CompositeDisposable()

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
            .withLatestFrom(reportTypeChanges()) { model, handler -> model to handler }
            .switchMap {
                callApiToEdit(it)
                        .doOnComplete { view.close() }
                        .catchOnError {
                            when (it) {
                                is EmptyProjectError -> view.showEmptyProjectError()
                                is EmptyDescriptionError -> view.showEmptyDescriptionError()
                                else -> view.showError(it)
                            }
                        }
                        .toObservable<Unit>()
            }

    private fun removeReportClicks() = view.removeReportClicks()
            .switchMap {
                callApiToRemove(report.id)
                        .doOnComplete { view.close() }
                        .catchOnError { view.showError(it) }
                        .toObservable<Unit>()
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
        ReportType.PAID_CONFERENCE -> paidConferenceReportEditHandler
    }

    private fun callApiToEdit(modelCallPair: Pair<ReportViewModel, (ReportViewModel) -> Completable>) =
            modelCallPair.second(modelCallPair.first)
                    .subscribeOn(schedulers.backgroundScheduler)
                    .observeOn(schedulers.uiScheduler)
                    .addLoader()

    private fun callApiToRemove(reportId: Long) =
            api.removeReport(reportId)
                    .subscribeOn(schedulers.backgroundScheduler)
                    .observeOn(schedulers.uiScheduler)
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
        ReportType.UNPAID_VACATIONS -> view.showUnpaidVacationsForm()
        ReportType.SICK_LEAVE -> view.showSickLeaveForm()
        ReportType.PAID_CONFERENCE -> view.showPaidConferenceForm()
    }

    private val regularReportEditHandler = { model: ReportViewModel ->
        (model as RegularViewModel).run {
            when {
                project === null -> Completable.error(EmptyProjectError())
                description.isBlank() -> Completable.error(EmptyDescriptionError())
                else -> editRegularReport(this, project)
            }
        }
    }

    private val paidVacationReportEditHandler = { model: ReportViewModel ->
        (model as PaidVacationsViewModel).let {
            api.editReport(report.id, ReportType.PAID_VACATIONS.id, model.selectedDate, model.hours, null, null)
        }
    }

    private val unpaidVacationReportEditHandler = { model: ReportViewModel ->
        api.editReport(report.id, ReportType.UNPAID_VACATIONS.id, model.selectedDate, null, null, null)
    }

    private val sickLeaveReportEditHandler = { model: ReportViewModel ->
        api.editReport(report.id, ReportType.SICK_LEAVE.id, model.selectedDate, null, null, null)
    }

    private val paidConferenceReportEditHandler = { model: ReportViewModel ->
        api.editReport(report.id, ReportType.PAID_CONFERENCE.id, model.selectedDate, null, null, null)
    }

    private fun editRegularReport(model: RegularViewModel, project: Project) =
            api.editReport(report.id, ReportType.REGULAR.id, model.selectedDate, model.hours, model.description, project.id)

    private fun Completable.addLoader() = this
            .doOnSubscribe { view.showLoader() }
            .doFinally { view.hideLoader() }
}

private val Report.type: ReportType
    get() = when (this) {
        is RegularHourlyReport -> ReportType.REGULAR
        is PaidVacationHourlyReport -> ReportType.PAID_VACATIONS
        is DailyReport -> when (reportType) {
            DailyReportType.UNPAID_VACATIONS -> ReportType.UNPAID_VACATIONS
            DailyReportType.SICK_LEAVE -> ReportType.SICK_LEAVE
            DailyReportType.PAID_CONFERENCE -> ReportType.PAID_CONFERENCE
        }
    }

class EmptyProjectError : NullPointerException()
class EmptyDescriptionError : IllegalArgumentException()