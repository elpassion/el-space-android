package pl.elpassion.elspace.hub.report.edit

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers.trampoline
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.*

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val reportTypeChanges = PublishSubject.create<ReportType>()
    private val editReportClicks = PublishSubject.create<ReportViewModel>()
    private val removeReportClicks = PublishSubject.create<Unit>()
    private val api = mock<ReportEdit.Api>()
    private val editReportSubject = CompletableSubject.create()
    private val removeReportSubject = CompletableSubject.create()
    private val subscribeOn = TestScheduler()
    private val observeOn = TestScheduler()

    @Before
    fun setUp() {
        whenever(api.editReport(any(), any(), any(), any(), any(), any())).thenReturn(editReportSubject)
        whenever(api.removeReport(any())).thenReturn(removeReportSubject)
        whenever(view.reportTypeChanges()).thenReturn(reportTypeChanges)
        whenever(view.editReportClicks()).thenReturn(editReportClicks)
        whenever(view.removeReportClicks()).thenReturn(removeReportClicks)
    }

    @Test
    fun shouldShowReportTypeOnCreate() {
        createController(newRegularHourlyReport()).onCreate()
        verify(view).showReportType(ReportType.REGULAR)
    }

    @Test
    fun shouldShowReportDateOnCreate() {
        createController(newRegularHourlyReport(year = 2017, month = 1, day = 1)).onCreate()
        verify(view).showDate("2017-01-01")
    }

    @Test
    fun shouldShowUpdatedReportDateOnReportChange() {
        createController(newDailyReport()).run {
            onCreate()
            onDateChanged("2017-01-02")
        }
        verify(view).showDate("2017-01-02")
    }

    @Test
    fun shouldShowReportedHoursOnCreateWhenEditingHourlyReport() {
        createController(newRegularHourlyReport(reportedHours = 8.0)).onCreate()
        verify(view).showReportedHours(8.0)
    }

    @Test
    fun shouldShowProjectNameOnCreateWhenEditingRegularReport() {
        val project = newProject(name = "Slack Time")
        createController(newRegularHourlyReport(project = project)).onCreate()
        verify(view).showProject(project)
    }

    @Test
    fun shouldShowUpdatedProjectNameOnProjectChange() {
        val project = newProject(name = "New project")
        createController(newDailyReport()).run {
            onCreate()
            onProjectChanged(project)
        }
        verify(view).showProject(project)
    }

    @Test
    fun shouldShowDescriptionOnCreateWhenEditingRegularReport() {
        createController(newRegularHourlyReport(description = "EL Space")).onCreate()
        verify(view).showDescription("EL Space")
    }

    @Test
    fun shouldShowEditedReportTypeOnCreate() {
        createController(newRegularHourlyReport()).onCreate()
        verify(view).showRegularForm()
    }

    @Test
    fun shouldShowRegularFormAfterReportTypeChangedToRegularReport() {
        createController(newDailyReport()).onCreate()
        reportTypeChanges.onNext(ReportType.REGULAR)
        verify(view).showRegularForm()
    }

    @Test
    fun shouldShowPaidVacationsFormAfterReportTypeChangedToPaidVacations() {
        createController(newDailyReport()).onCreate()
        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        verify(view).showPaidVacationsForm()
    }

    @Test
    fun shouldShowUnpaidVacationsFormAfterReportTypeChangedToUnpaidVacations() {
        createController().onCreate()
        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).showUnpaidVacationsForm()
    }

    @Test
    fun shouldShowSickLeaveFormAfterReportTypeChangedToSickLeave() {
        createController().onCreate()
        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).showSickLeaveForm()
    }

    @Test
    fun shouldShowPaidConferenceFormAfterReportTypeChangedToPaidConference() {
        createController().onCreate()
        reportTypeChanges.onNext(ReportType.PAID_CONFERENCE)
        verify(view).showPaidConferenceForm()
    }

    @Test
    fun shouldEditRegularReportWithChangedData() {
        val report = newRegularHourlyReport(id = 7, reportedHours = 8.0, year = 2017, month = 1, day = 1, project = newProject(id = 11))
        createController(report).onCreate()
        onEditReportClick(model = RegularViewModel("2017-01-02", newProject(id = 12), "Slack Time", "8"))
        verify(api).editReport(7, 0, "2017-01-02", "8", "Slack Time", 12)
    }

    @Test
    fun shouldEditPaidVacationsWithChangedData() {
        createController(newPaidVacationHourlyReport(id = 8, year = 2015, month = 3, day = 3, reportedHours = 2.0)).onCreate()
        onEditReportClick(model = PaidVacationsViewModel("2015-03-03", "3"))
        verify(api).editReport(8, 1, "2015-03-03", "3", null, null)
    }

    @Test
    fun shouldEditUnpaidVacationsWithChangedData() {
        createController(newDailyReport(reportType = DailyReportType.UNPAID_VACATIONS, id = 70, year = 2010, month = 5, day = 15)).onCreate()
        onEditReportClick(model = DailyViewModel("2010-05-20"))
        verify(api).editReport(70, 2, "2010-05-20", null, null, null)
    }

    @Test
    fun shouldEditSickLeaveWithChangedData() {
        createController(newDailyReport(reportType = DailyReportType.SICK_LEAVE, id = 3, year = 2000, month = 3, day = 7)).onCreate()
        onEditReportClick(model = DailyViewModel("2000-03-08"))
        verify(api).editReport(3, 3, "2000-03-08", null, null, null)
    }

    @Test
    fun shouldEditPaidConferenceWithChangedData() {
        createController(newDailyReport(reportType = DailyReportType.PAID_CONFERENCE, id = 66, year = 2007, month = 4, day = 1)).onCreate()
        onEditReportClick(model = DailyViewModel("2007-04-09"))
        verify(api).editReport(66, 4, "2007-04-09", null, null, null)
    }

    @Test
    fun shouldCloseAfterReportEdited() {
        createController().onCreate()
        onEditReportClick()
        completeReportEdit()
        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnEditingReport() {
        createController().onCreate()
        onEditReportClick()
        verify(view).showLoader()
    }

    @Test
    fun shouldShowErrorWhenEditingReportFails() {
        createController().onCreate()
        onEditReportClick()
        editReportSubject.onError(RuntimeException())
        verify(view).showError(any())
    }

    @Test
    fun shouldHideLoaderWhenEditingReportTerminated() {
        createController().run {
            onCreate()
            onEditReportClick()
            onDestroy()
        }
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnReportEdited() {
        createController().onCreate()
        onEditReportClick()
        completeReportEdit()
        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowEmptyProjectErrorWhenNoProjectSelectedInRegularForm() {
        createController(newDailyReport()).onCreate()
        reportTypeChanges.onNext(ReportType.REGULAR)
        onEditReportClick(model = RegularViewModel("2000-02-03", null, "Slack Time", "8"))
        verify(view).showEmptyProjectError()
    }

    @Test
    fun shouldShowEmptyDescriptionErrorWhenDescriptionIsEmptyInRegularForm() {
        createController(newDailyReport()).onCreate()
        reportTypeChanges.onNext(ReportType.REGULAR)
        onEditReportClick(model = RegularViewModel("2000-02-03", newProject(), "", "8"))
        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldRemoveReportOnRemoveClick() {
        createController(newRegularHourlyReport(id = 123)).onCreate()
        onRemoveReportClick()
        verify(api).removeReport(123)
    }

    @Test
    fun shouldShowLoaderOnRemovingReport() {
        createController().onCreate()
        onRemoveReportClick()
        verify(view).showLoader()
    }

    @Test
    fun shouldShowErrorWhenRemovingReportFails() {
        createController().onCreate()
        onRemoveReportClick()
        removeReportSubject.onError(RuntimeException())
        verify(view).showError(any())
    }

    @Test
    fun shouldHideLoaderWhenRemovingReportTerminated() {
        createController().run {
            onCreate()
            onRemoveReportClick()
            onDestroy()
        }
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnReportRemoved() {
        createController().onCreate()
        onRemoveReportClick()
        completeReportRemove()
        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldSubscribeToEditOnGivenScheduler() {
        createController(subscribeOnScheduler = subscribeOn).onCreate()
        onEditReportClick()
        completeReportEdit()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldObserveEditOnGivenScheduler() {
        createController(observeOnScheduler = observeOn).onCreate()
        onEditReportClick()
        completeReportEdit()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldSubscribeToRemoveOnGivenScheduler() {
        createController(subscribeOnScheduler = subscribeOn).onCreate()
        onRemoveReportClick()
        completeReportRemove()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldObserveRemoveOnGivenScheduler() {
        createController(observeOnScheduler = observeOn).onCreate()
        onRemoveReportClick()
        completeReportRemove()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowErrorTwiceOnRemoveReportCallFailures() {
        val runtimeException = RuntimeException()
        createController().onCreate()
        onRemoveReportClick()
        verify(view, never()).showError(any())
        removeReportSubject.onError(runtimeException)
        verify(view).showError(runtimeException)
        onRemoveReportClick()
        removeReportSubject.onError(runtimeException)
        verify(view, times(2)).showError(runtimeException)
    }

    @Test
    fun shouldShowErrorTwiceOnEditReportCallFailures() {
        val runtimeException = RuntimeException()
        createController().onCreate()
        onEditReportClick()
        verify(view, never()).showError(any())
        editReportSubject.onError(runtimeException)
        verify(view).showError(runtimeException)
        onEditReportClick()
        editReportSubject.onError(runtimeException)
        verify(view, times(2)).showError(runtimeException)
    }

    private fun createController(report: Report = newRegularHourlyReport(),
                                 subscribeOnScheduler: Scheduler = trampoline(),
                                 observeOnScheduler: Scheduler = trampoline()) =
            ReportEditController(report, view, api, SchedulersSupplier(subscribeOnScheduler, observeOnScheduler))

    private fun onEditReportClick(model: ReportViewModel = RegularViewModel("2000-01-01", newProject(), "Desc", "8")) {
        editReportClicks.onNext(model)
    }

    private fun onRemoveReportClick() {
        removeReportClicks.onNext(Unit)
    }

    private fun completeReportEdit() = editReportSubject.onComplete()

    private fun completeReportRemove() = removeReportSubject.onComplete()

}