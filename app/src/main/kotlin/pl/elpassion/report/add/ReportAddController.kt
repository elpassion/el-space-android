package pl.elpassion.report.add

import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getDateString
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.project.last.LastSelectedProjectRepository
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.util.Calendar.*

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
        Observable.merge(projectClickEvents(), addReportClicks(), reportTypeChanges())
                .subscribe()
                .save()
    }

    private fun reportTypeChanges() = view.reportTypeChanges()
            .doOnNext { onReportTypeChanged(it) }
            .map { Unit }

    private fun addReportClicks() = view.addReportClicks()
            .switchMap { handleNewReport(it) }
            .doOnError { view.showError(it) }
            .onErrorResumeNext { Observable.empty() }

    private fun projectClickEvents(): Observable<Unit> {
        return view.projectClickEvents()
                .doOnNext {
                    view.openProjectChooser()
                }
    }

    private fun handleNewReport(it: ReportViewModel): Observable<Unit> {
        return when (it) {
            is RegularReport -> Observable.merge(emptyDescriptionErrorFlow(it), emptyProjectErrorFlow(it), validReportFlow(it))
            is UnpaidVacationsReport -> api.addUnpaidVacationsReport(it.selectedDate).toObservable<Unit>().addLoader().doOnCompleted { view.close() }
            is PaidVacationsReport -> api.addPaidVacationsReport(it.selectedDate, it.hours).toObservable<Unit>().addLoader().doOnCompleted { view.close() }
            is SickLeaveReport -> api.addSickLeaveReport(it.selectedDate).toObservable<Unit>().addLoader().doOnCompleted { view.close() }
            else -> Observable.error(IllegalArgumentException(it.toString()))
        }
    }

    private fun emptyDescriptionErrorFlow(it: RegularReport): Observable<Unit> = Observable.just(it)
            .filter { !it.hasDescription() }
            .doOnNext { view.showEmptyDescriptionError() }
            .map { Unit }

    private fun emptyProjectErrorFlow(it: RegularReport): Observable<Unit> = Observable.just(it)
            .filter { !it.hasProject() }
            .doOnNext { view.showEmptyProjectError() }
            .map { Unit }

    private fun validReportFlow(it: RegularReport) = Observable.just(it)
            .filter { it.hasProject() && it.hasDescription() }
            .switchMap {
                api.addRegularReport(it.selectedDate, it.project!!.id, it.hours, it.description)
                        .toObservable<Unit>()
                        .addLoader()
                        .doOnCompleted { view.close() }
            }

    private fun RegularReport.hasDescription() = description.isNotBlank()

    private fun RegularReport.hasProject() = project != null

    private fun <T> Observable<T>.addLoader() =
            doOnSubscribe { view.showLoader() }
                    .doOnUnsubscribe { view.hideLoader() }
                    .doOnTerminate { view.hideLoader() }

    private fun getCurrentDatePerformedAtString(): String {
        val currentCalendar = getTimeFrom(timeInMillis = CurrentTimeProvider.get())
        return getDateString(currentCalendar.get(YEAR), currentCalendar.get(MONTH) + 1, currentCalendar.get(DAY_OF_MONTH))
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    private fun onReportTypeChanged(reportType: ReportType) {
        when (reportType) {
            ReportType.REGULAR -> showRegularForm()
            ReportType.PAID_VACATIONS -> showPaidVacationsForm()
            ReportType.SICK_LEAVE -> showSickLeaveForm()
            ReportType.UNPAID_VACATIONS -> showUnpaidVacationsForm()
        }
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
}
