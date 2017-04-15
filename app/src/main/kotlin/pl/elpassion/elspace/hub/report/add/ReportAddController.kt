package pl.elpassion.elspace.hub.report.add

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.*
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
import pl.elpassion.elspace.hub.report.PaidVacationsViewModel
import pl.elpassion.elspace.hub.report.RegularViewModel
import pl.elpassion.elspace.hub.report.ReportType
import pl.elpassion.elspace.hub.report.ReportViewModel

class ReportAddController(private val date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api,
                          private val repository: LastSelectedProjectRepository,
                          private val schedulers: SchedulersSupplier) {

    private val subscriptions = CompositeDisposable()

    fun onCreate() {
        repository.getLastProject()?.let {
            view.showSelectedProject(it)
        }
        view.showDate(date ?: getCurrentDatePerformedAtString())
        view.projectClickEvents()
                .subscribe { view.openProjectChooser() }
                .addTo(subscriptions)

        addReportClicks()
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
        view.showSelectedProject(project)
    }

    private fun getCurrentDatePerformedAtString() = getTimeFrom(timeInMillis = CurrentTimeProvider.get()).getDateString()

    private fun addReportClicks() = view.addReportClicks()
            .withLatestFrom(reportTypeChanges()) { model, handler -> model to handler }
            .switchMap { callApi(it) }
            .doOnNext { view.close() }

    private fun reportTypeChanges() = view.reportTypeChanges()
            .doOnNext { onReportTypeChanged(it) }
            .startWith(ReportType.REGULAR)
            .map { chooseReportHandler(it) }

    private fun chooseReportHandler(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> regularReportHandler
        ReportType.PAID_VACATIONS -> paidVacationReportHandler
        ReportType.SICK_LEAVE -> sickLeaveReportHandler
        ReportType.UNPAID_VACATIONS -> unpaidVacationReportHandler
    }

    private fun callApi(modelCallPair: Pair<ReportViewModel, (ReportViewModel) -> Observable<Unit>>) = modelCallPair.second(modelCallPair.first)
            .subscribeOn(schedulers.subscribeOn)
            .observeOn(schedulers.observeOn)
            .addLoader()
            .catchOnError { view.showError(it) }

    private val regularReportHandler = { model: ReportViewModel ->
        (model as RegularViewModel).run {
            when {
                hasNoProject() -> {
                    view.showEmptyProjectError()
                    Observable.empty()
                }
                hasNoDescription() -> {
                    view.showEmptyDescriptionError()
                    Observable.empty()
                }
                else -> addRegularReportObservable(this)
            }
        }
    }

    private fun addRegularReportObservable(model: RegularViewModel) =
            api.addRegularReport(model.selectedDate, model.project!!.id, model.hours, model.description)

    private val paidVacationReportHandler = { model: ReportViewModel ->
        api.addPaidVacationsReport(model.selectedDate, (model as PaidVacationsViewModel).hours)
    }

    private val sickLeaveReportHandler = { model: ReportViewModel ->
        api.addSickLeaveReport(model.selectedDate)
    }

    private val unpaidVacationReportHandler = { model: ReportViewModel ->
        api.addUnpaidVacationsReport(model.selectedDate)
    }

    private fun onReportTypeChanged(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> view.showRegularForm()
        ReportType.PAID_VACATIONS -> view.showPaidVacationsForm()
        ReportType.SICK_LEAVE -> view.showSickLeaveForm()
        ReportType.UNPAID_VACATIONS -> view.showUnpaidVacationsForm()
    }

    private fun RegularViewModel.hasNoDescription() = description.isBlank()

    private fun RegularViewModel.hasNoProject() = project == null

    private fun Observable<Unit>.addLoader() = this
            .doOnSubscribe { view.showLoader() }
            .doFinally { view.hideLoader() }
}
