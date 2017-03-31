package pl.elpassion.elspace.hub.report.edit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.*
import rx.schedulers.Schedulers.trampoline
import rx.subjects.PublishSubject

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val reportTypeChanges = PublishSubject.create<ReportType>()
    private val editReportClicks = PublishSubject.create<ReportViewModel>()
    private val removeReportClicks = PublishSubject.create<Long>()
    private val api = mock<ReportEdit.Api>()
    private val editReportSubject = PublishSubject.create<Unit>()
    private val removeReportSubject = PublishSubject.create<Unit>()

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
    fun shouldShowSickLeaveFormAfterReportTypeChangedToSickLeave() {
        createController().onCreate()
        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).showSickLeaveForm()
    }

    @Test
    fun shouldShowUnpaidVacationsFormAfterReportTypeChangedToUnpaidVacations() {
        createController().onCreate()
        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).showUnpaidVacationsForm()
    }

    @Test
    fun shouldEditRegularReportWithChangedData() {
        val report = newRegularHourlyReport(id = 7, reportedHours = 8.0, year = 2017, month = 1, day = 1, project = newProject(id = 11))
        createController(report).onCreate()
        onEditReportClick(model = RegularReport("2017-01-02", newProject(id = 12), "Slack Time", "8"))
        verify(api).editReport(7, 0, "2017-01-02", "8", "Slack Time", 12)
    }

    @Test
    fun shouldEditPaidVacationsWithChangedData() {
        createController(newPaidVacationHourlyReport(id = 8, year = 2015, month = 3, day = 3, reportedHours = 2.0)).onCreate()
        onEditReportClick(model = PaidVacationsReport("2015-03-03", "3"))
        verify(api).editReport(8, 1, "2015-03-03", "3", null, null)
    }

    @Test
    fun shouldEditUnpaidVacationsWithChangedData() {
        createController(newDailyReport(reportType = DailyReportType.UNPAID_VACATIONS, id = 70, year = 2010, month = 5, day = 15)).onCreate()
        onEditReportClick(model = UnpaidVacationsReport("2010-05-20"))
        verify(api).editReport(70, 2, "2010-05-20", null, null, null)
    }

    @Test
    fun shouldEditSickLeaveWithChangedData() {
        createController(newDailyReport(reportType = DailyReportType.SICK_LEAVE, id = 3, year = 2000, month = 3, day = 7)).onCreate()
        onEditReportClick(model = SickLeaveReport("2000-03-08"))
        verify(api).editReport(3, 3, "2000-03-08", null, null, null)
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
        onEditReportClick(model = RegularReport("2000-02-03", null, "Slack Time", "8"))
        verify(view).showEmptyProjectError()
    }

    @Test
    fun shouldShowEmptyDescriptionErrorWhenDescriptionIsEmptyInRegularForm() {
        createController(newDailyReport()).onCreate()
        reportTypeChanges.onNext(ReportType.REGULAR)
        onEditReportClick(model = RegularReport("2000-02-03", newProject(), "", "8"))
        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldRemoveReportOnRemoveClick() {
        createController(newRegularHourlyReport(id = 123)).onCreate()
        onRemoveReportClick(123)
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

    private fun createController(report: Report = newRegularHourlyReport()) =
            ReportEditController(report, view, api, SchedulersSupplier(trampoline(), trampoline()))

    private fun onEditReportClick(model: ReportViewModel = RegularReport("2000-01-01", newProject(), "Desc", "8")) {
        editReportClicks.onNext(model)
    }

    private fun onRemoveReportClick(reportId: Long = 1) {
        removeReportClicks.onNext(reportId)
    }

    private fun completeReportEdit() = editReportSubject.run {
        onNext(Unit)
        onCompleted()
    }
}