package pl.elpassion.elspace.hub.report.add

import pl.elpassion.elspace.api.applySchedulers

import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.extensions.getDateString
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
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
        projectClickEvents()
                .subscribe()
                .save()
        addReportClicks()
                .subscribe()
                .save()
    }

    private fun getCurrentDatePerformedAtString() = getTimeFrom(timeInMillis = CurrentTimeProvider.get()).getDateString()

    private fun projectClickEvents() = view.projectClickEvents()
            .doOnNext { view.openProjectChooser() }

    private fun addReportClicks() = view.addReportClicks().withLatestFrom(reportTypeChanges(), { a, b -> a to b })
            .switchMap { callApi(it) }
            .doOnError { view.showError(it) }
            .onErrorResumeNext { Observable.empty() }

    private fun callApi(it: Pair<ReportViewModel, (ReportViewModel) -> Observable<Unit>>) = it.second(it.first)
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
                Observable.empty<Unit>()
            } else if (!it.hasDescription()) {
                view.showEmptyDescriptionError()
                Observable.empty<Unit>()
            } else {
                addRegularReportObservable(it)
            }
        }
    }

    private fun addRegularReportObservable(regularReport: RegularReport) =
            api.addRegularReport(regularReport.selectedDate, regularReport.project!!.id, regularReport.hours, regularReport.description)
                    .toObservable<Unit>()
                    .doOnCompleted { view.close() }

    private val unpaidVacationReportHandler = { unpaidVacationsReport: ReportViewModel ->
        api.addUnpaidVacationsReport(unpaidVacationsReport.selectedDate)
                .toObservable<Unit>()
                .doOnCompleted { view.close() }
    }

    private val paidVacationReportHandler = { paidVacationsReport: ReportViewModel ->
        api.addPaidVacationsReport(paidVacationsReport.selectedDate, (paidVacationsReport as PaidVacationsReport).hours)
                .toObservable<Unit>()
                .doOnCompleted { view.close() }
    }

    private val sickLeaveReportHandler = { sickLeaveReport: ReportViewModel ->
        api.addSickLeaveReport(sickLeaveReport.selectedDate)
                .toObservable<Unit>()
                .doOnCompleted { view.close() }
    }

    private fun onReportTypeChanged(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> showRegularForm()
        ReportType.PAID_VACATIONS -> showPaidVacationsForm()
        ReportType.SICK_LEAVE -> showSickLeaveForm()
        ReportType.UNPAID_VACATIONS -> showUnpaidVacationsForm()
    }

    private fun RegularReport.hasDescription() = description.isNotBlank()

    private fun RegularReport.hasProject() = project != null

    private fun <T> Observable<T>.addLoader() = this
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
