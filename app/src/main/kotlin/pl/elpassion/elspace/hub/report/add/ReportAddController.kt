package pl.elpassion.elspace.hub.report.add

import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.common.extensions.catchOnError
import pl.elpassion.elspace.common.extensions.getDateString
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
import pl.elpassion.elspace.hub.report.ReportType
import rx.Completable
import rx.subscriptions.CompositeSubscription

class ReportAddController(private val date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api,
                          private val repository: LastSelectedProjectRepository,
                          private val schedulers: SchedulersSupplier) {

    private val subscriptions = CompositeSubscription()

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
            .withLatestFrom(reportTypeChanges(), { model, handler -> model to handler })
            .switchMap { callApi(it).toSingleDefault(Unit).toObservable() }
            .doOnNext { view.close() }
            .catchOnError { view.showError(it) }

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

    private fun callApi(modelCallPair: Pair<ReportViewModel, (ReportViewModel) -> Completable>) = modelCallPair.second(modelCallPair.first)
            .subscribeOn(schedulers.subscribeOn)
            .observeOn(schedulers.observeOn)
            .addLoader()

    private val regularReportHandler = { regularReport: ReportViewModel ->
        (regularReport as RegularReport).let {
            when {
                !regularReport.hasProject() -> {
                    view.showEmptyProjectError()
                    Completable.never()
                }
                !regularReport.hasDescription() -> {
                    view.showEmptyDescriptionError()
                    Completable.never()
                }
                else -> addRegularReportObservable(it)
            }
        }
    }

    private fun addRegularReportObservable(regularReport: RegularReport) =
            api.addRegularReport(regularReport.selectedDate, regularReport.project!!.id, regularReport.hours, regularReport.description)

    private val paidVacationReportHandler = { paidVacationsReport: ReportViewModel ->
        api.addPaidVacationsReport(paidVacationsReport.selectedDate, (paidVacationsReport as PaidVacationsReport).hours)
    }

    private val sickLeaveReportHandler = { sickLeaveReport: ReportViewModel ->
        api.addSickLeaveReport(sickLeaveReport.selectedDate)
    }

    private val unpaidVacationReportHandler = { unpaidVacationsReport: ReportViewModel ->
        api.addUnpaidVacationsReport(unpaidVacationsReport.selectedDate)
    }

    private fun onReportTypeChanged(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> showRegularForm()
        ReportType.PAID_VACATIONS -> showPaidVacationsForm()
        ReportType.SICK_LEAVE -> showSickLeaveForm()
        ReportType.UNPAID_VACATIONS -> showUnpaidVacationsForm()
    }

    private fun RegularReport.hasDescription() = description.isNotBlank()

    private fun RegularReport.hasProject() = project != null

    private fun Completable.addLoader() = this
            .doOnSubscribe { view.showLoader() }
            .doOnUnsubscribe { view.hideLoader() }
            .doOnTerminate { view.hideLoader() }

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
