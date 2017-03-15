package pl.elpassion.elspace.hub.report.edit

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
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
        val report = newRegularHourlyReport(year = 2017, month = 1, day = 1)
        ReportEditController(report, view).onCreate()
        verify(view).showDate("2017-01-01")
    }

    @Test
    fun shouldShowRegularFormAfterReportTypeChangedToRegularReport() {
        ReportEditController(newDailyReport(), view).onCreate()
        reportTypeChanges.onNext(ReportType.REGULAR)
        verify(view).showRegularForm()
    }

    @Test
    fun shouldShowPaidVacationsFormAfterReportTypeChangedToPaidVacations() {
        ReportEditController(newDailyReport(), view).onCreate()
        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        verify(view).showPaidVacationsForm()
    }

    @Test
    fun shouldShowSickLeaveFormAfterReportTypeChangedToSickLeave() {
        ReportEditController(newDailyReport(), view).onCreate()
        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).showSickLeaveForm()
    }

    @Test
    fun shouldUnpaidVacationsFormAfterReportTypeChangedToUnpaidVacations() {
        ReportEditController(newDailyReport(), view).onCreate()
        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).showUnpaidVacationsForm()
    }
}