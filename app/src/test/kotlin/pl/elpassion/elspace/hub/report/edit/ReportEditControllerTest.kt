package pl.elpassion.elspace.hub.report.edit

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportType
import rx.subjects.PublishSubject

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val reportTypeChanges = PublishSubject.create<ReportType>()

    @Before
    fun setUp() {
        whenever(view.reportTypeChanges()).thenReturn(reportTypeChanges)
    }

    @Test
    fun shouldShowReportDateOnCreate() {
        createController(newRegularHourlyReport(year = 2017, month = 1, day = 1)).onCreate()
        verify(view).showDate("2017-01-01")
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

    private fun createController(report: Report) = ReportEditController(report, view)
}