package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.project.dto.newPaidVacationHourlyReport
import pl.elpassion.project.dto.newRegularHourlyReport
import pl.elpassion.report.DailyReportType
import pl.elpassion.report.list.service.ReportDayService
import rx.Observable
import java.util.*

class ReportListControllerTest {

    val service = mock<ReportDayService>()
    val actions = mock<ReportList.Actions>()
    val view = mock<ReportList.View>()
    val controller = ReportListController(service, actions, view)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubCurrentTime()
    }

    @Test
    fun shouldShowCorrectMonthNameOnCreate() {
        stubServiceToReturnNever()
        stubDateChangeToReturn(getTimeFrom(2016, 0, 20))

        controller.onCreate()

        verify(view).showMonthName("January")
    }

    @Test
    fun shouldReallyShowCorrectMonthNameOnCreate() {
        stubServiceToReturnEmptyList()
        stubDateChangeToReturn(getTimeFrom(2016, 10, 20))

        controller.onCreate()

        verify(view).showMonthName("November")
    }

    @Test
    fun shouldShowErrorWhenApiCallFails() {
        stubServiceToReturnError()

        controller.onCreate()

        verify(view, times(1)).showError(any())
    }

    @Test
    fun shouldShowLoaderWhenApiCallBegins() {
        stubServiceToReturnNever()

        controller.onCreate()

        verify(view, times(1)).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenCallIsNotFinishedOnDestroy() {
        stubServiceToReturnNever()

        controller.onCreate()
        controller.onDestroy()

        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCall() {
        stubServiceToReturnEmptyListAndNeverEnd()

        controller.onCreate()

        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallAndFinishes() {
        stubServiceToReturnEmptyList()

        controller.onCreate()

        verify(view, atLeast(1)).hideLoader()
    }

    @Test
    fun shouldOpenAddReportScreenOnDay() {
        controller.onDayDate(date = "1999-01-02")

        verify(view, times(1)).openAddReportScreen("1999-01-02")
    }

    @Test
    fun shouldOpenEditRegularReportScreenOnReportIfReportIsRegularReport() {
        val report = newRegularHourlyReport()
        controller.onReport(report)

        verify(view, times(1)).openEditReportScreen(report)
    }

    @Test
    fun shouldOpenEditPaidVacationScreenOnReportIfReportIsPaidVacationReport() {
        val report = newPaidVacationHourlyReport()
        controller.onReport(report)

        verify(view, times(1)).openPaidVacationEditReportScreen(report)
    }

    @Test
    fun shouldOpenEditDailyScreenOnReportIfReportIsDailyReport() {
        val report = newDailyReport(reportType = DailyReportType.SICK_LEAVE)
        controller.onReport(report)

        verify(view, times(1)).openDailyEditReportScreen(report)
    }

    @Test
    fun shouldOpenAddReportScreen() {
        controller.onAddTodayReport()

        verify(view).openAddReportScreen()
    }

    @Test
    fun shouldScrollToCorrectPositionOnTodayWhenNoReports() {
        stubServiceToReturnEmptyList()
        stubCurrentTime(2017, 1, 20)

        controller.onCreate()
        controller.updateTodayPosition(20)
        controller.onToday()

        verify(view).scrollToPosition(20)
    }

    @Test
    fun shouldReallyScrollToCorrectPositionOnTodayWhenNoReports() {
        stubServiceToReturnEmptyList()
        stubCurrentTime(2017, 1, 31)

        controller.onCreate()
        controller.updateTodayPosition(31)
        controller.onToday()

        verify(view).scrollToPosition(31)
    }

    @Test
    fun shouldListenForFilterActions() {
        stubServiceToReturnEmptyList()

        controller.onCreate()

        verify(actions).shouldFilterReports()
    }

    private fun stubServiceToReturnNever() {
        whenever(service.createDays(any())).thenReturn(Observable.never())
    }

    private fun stubServiceToReturnEmptyList() {
        whenever(service.createDays(any())).thenReturn(Observable.just(listOf()))
    }

    private fun stubServiceToReturnEmptyListAndNeverEnd() {
        val observable = Observable.just<List<Day>>(listOf()).concatWith(Observable.never())
        whenever(service.createDays(any())).thenReturn(observable)
    }

    private fun stubServiceToReturnError() {
        whenever(service.createDays(any())).thenReturn(Observable.error(RuntimeException()))
    }

    private fun stubDateChangeToReturn(cal: Calendar) {
        CurrentTimeProvider.override = { cal.timeInMillis }
    }

}
