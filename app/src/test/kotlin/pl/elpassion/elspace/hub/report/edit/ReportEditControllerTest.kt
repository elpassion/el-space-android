package pl.elpassion.elspace.hub.report.edit

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.RegularReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.ReportType
import pl.elpassion.elspace.hub.report.ReportViewModel
import rx.subjects.PublishSubject

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val reportTypeChanges = PublishSubject.create<ReportType>()
    private val editReportClicks = PublishSubject.create<ReportViewModel>()
    private val api = mock<ReportEdit.Api>()

    @Before
    fun setUp() {
        whenever(view.reportTypeChanges()).thenReturn(reportTypeChanges)
        whenever(view.editReportClicks()).thenReturn(editReportClicks)
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
        createController(newRegularHourlyReport(project = newProject(name = "Slack Time"))).onCreate()
        verify(view).showProjectName("Slack Time")
    }

    @Test
    fun shouldShowUpdatedProjectNameOnProjectChange() {
        createController(newDailyReport()).run {
            onCreate()
            onProjectChanged(newProject(name = "New project"))
        }
        verify(view).showProjectName("New project")
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
        createController(newRegularHourlyReport()).onCreate()
        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).showSickLeaveForm()
    }

    @Test
    fun shouldShowUnpaidVacationsFormAfterReportTypeChangedToUnpaidVacations() {
        createController(newRegularHourlyReport()).onCreate()
        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).showUnpaidVacationsForm()
    }

    @Test
    fun shouldEditRegularReportWithChangedData() {
        val report = newRegularHourlyReport(id = 7, reportedHours = 8.0, year = 2017, month = 1, day = 1, project = newProject(id = 11))
        createController(report).onCreate()
        editReportClicks.onNext(RegularReport("2017-01-02", newProject(id = 12), "Slack Time", "8"))
        verify(api).editReport(7, 0, "2017-01-02", "8", "Slack Time", 12)
    }

    private fun createController(report: Report) = ReportEditController(report, view, api)
}