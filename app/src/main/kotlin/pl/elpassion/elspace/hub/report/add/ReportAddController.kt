package pl.elpassion.elspace.hub.report.add

import pl.elpassion.elspace.api.applySchedulers

import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.extensions.getDateString
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
import rx.Completable
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class ReportAddController(private val date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api,
                          private val repository: LastSelectedProjectRepository) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        repository.getLastProject()?.let {
            view.showSelectedProject(it)
        }
        view.showDate(date ?: getCurrentDatePerformedAtString())
        view.projectClickEvents()
                .subscribe { view.openProjectChooser() }
                .save()

        addReportClicks()
                .subscribe()
                .save()
    }

    private fun getCurrentDatePerformedAtString() = getTimeFrom(timeInMillis = CurrentTimeProvider.get()).getDateString()

    private fun addReportClicks() = view.addReportClicks().withLatestFrom(reportTypeChanges(), { a, b -> a to b })
            .switchMap { callApi(it).toSingleDefault(Unit).toObservable() }
            .doOnNext { view.close() }
            .doOnError { view.showError(it) }
            .onErrorResumeNext { Observable.never() }

    private fun callApi(it: Pair<ReportViewModel, (ReportViewModel) -> Completable>) = it.second(it.first)
            .applySchedulers()
            .addLoader()

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

    private val regularReportHandler = { regularReport: ReportViewModel ->
        (regularReport as RegularReport).let {
            if (!it.hasProject()) {
                view.showEmptyProjectError()
                Completable.never()
            } else if (!it.hasDescription()) {
                view.showEmptyDescriptionError()
                Completable.never()
            } else {
                addRegularReportObservable(it)
            }
        }
    }

    private fun addRegularReportObservable(regularReport: RegularReport) =
            api.addRegularReport(regularReport.selectedDate, regularReport.project!!.id, regularReport.hours, regularReport.description)

    private val unpaidVacationReportHandler = { unpaidVacationsReport: ReportViewModel ->
        api.addUnpaidVacationsReport(unpaidVacationsReport.selectedDate)
    }

    private val paidVacationReportHandler = { paidVacationsReport: ReportViewModel ->
        api.addPaidVacationsReport(paidVacationsReport.selectedDate, (paidVacationsReport as PaidVacationsReport).hours)
    }

    private val sickLeaveReportHandler = { sickLeaveReport: ReportViewModel ->
        api.addSickLeaveReport(sickLeaveReport.selectedDate)
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

    fun onDestroy() {
        subscriptions.clear()
    }

    private fun showUnpaidVacationsForm() {
        view.hideHoursInput()
        view.hideDescriptionInput()
        view.hideProjectChooser()
        view.showUnpaidVacationsInfo()
    }

    private fun showSickLeaveForm() {
        view.hideHoursInput()
        view.hideDescriptionInput()
        view.hideProjectChooser()
        view.showSickLeaveInfo()
    }

    private fun showPaidVacationsForm() {
        view.showHoursInput()
        view.hideProjectChooser()
        view.hideDescriptionInput()
        view.hideAdditionalInfo()
    }

    private fun showRegularForm() {
        view.showDescriptionInput()
        view.showProjectChooser()
        view.showHoursInput()
        view.hideAdditionalInfo()
    }

    private fun Subscription.save() {
        subscriptions.add(this)
    }

    fun onDateChanged(date: String) {
        view.showDate(date)
    }
}
