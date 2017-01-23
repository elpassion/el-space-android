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
import pl.elpassion.report.list.service.DayFilter
import pl.elpassion.report.list.service.ReportDayService
import rx.Observable
import java.util.*

class ReportListControllerTest {

    val service = mock<ReportDayService>()
    val actions = mock<ReportList.Actions>()
    val filter = mock<DayFilter>()
    val view = mock<ReportList.View>()
    val controller = ReportListController(service, filter, actions, view)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubCurrentTime()
        stubViewActions()
    }

    @Test
    fun shouldShowCorrectMonthNameOnCreate() {
        stubServiceToReturnNever()
        stubDateChangeToReturn(getTimeFrom(2016, 0, 20))

        controller.onCreate()

        verify(view).showMonthName("January")
    }

    @Test
    fun shouldCallActionFilterPrevOnCreate() {
        stubServiceToReturnNever()

        controller.onCreate()

        verify(actions).reportsFilter()
    }

    @Test
    fun shouldCallActionMonthChangeNextOnCreate() {
        stubServiceToReturnNever()

        controller.onCreate()

        verify(actions).monthChangeToNext()
    }


    @Test
    fun shouldCallActionMonthChangePrevOnCreate() {
        stubServiceToReturnNever()

        controller.onCreate()

        verify(actions).monthChangeToPrev()
    }

    @Test
    fun shouldCallActionReportAddOnCreate() {
        stubServiceToReturnNever()

        controller.onCreate()

        verify(actions).reportAdd()
    }

    @Test
    fun shouldCallActionScrollToCurrentOnCreate() {
        stubServiceToReturnNever()

        controller.onCreate()

        verify(actions).scrollToCurrent()
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
    fun shouldShowLoaderWhenNotDuringPullRefresh() {
        stubServiceToReturnNever()
        whenever(view.isDuringPullToRefresh()).thenReturn(false)

        controller.onCreate()

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenCallIsNotFinishedOnDestroy() {
        stubServiceToReturnNever()

        controller.onCreate()
        controller.onDestroy()

        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallAndFinishes() {
        stubServiceToReturnEmptyList()

        controller.onCreate()

        verify(view).hideLoader()
    }

    @Test
    fun shouldNotShowLoaderWhenDuringPullRefresh() {
        stubServiceToReturnNever()
        whenever(view.isDuringPullToRefresh()).thenReturn(true)
        controller.onCreate()

        verify(view, never()).showLoader()
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
        whenever(actions.reportAdd()).thenReturn(Observable.just(Unit))

        controller.onCreate()

        verify(view).openAddReportScreen()
    }

    @Test
    fun shouldCallServiceTwiceWhenPullToRefreshCalledOnCreate() {
        stubServiceToReturnEmptyList()

        whenever(actions.refreshingEvents()).thenReturn(Observable.just(Unit))

        controller.onCreate()
        verify(service, times(2)).createDays(any())
    }

    @Test
    fun shouldScrollToCorrectPositionOnTodayWhenNoReports() {
        stubServiceToReturnEmptyList()
        stubCurrentTime(2017, 1, 20)
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))

        controller.updateTodayPosition(20)
        controller.onCreate()

        verify(view).scrollToPosition(20)
    }

    @Test
    fun shouldReallyScrollToCorrectPositionOnTodayWhenNoReports() {
        stubServiceToReturnEmptyList()
        stubCurrentTime(2017, 1, 31)
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))

        controller.updateTodayPosition(31)
        controller.onCreate()

        verify(view).scrollToPosition(31)
    }

    @Test
    fun shouldScrollToCorrectPositionAndChangeMonthOnTodayWhenMonthIsNotCurrent() {
        stubServiceToReturnEmptyList()
        stubCurrentTime(2017, 1, 10)
        whenever(actions.monthChangeToPrev()).thenReturn(Observable.just(Unit))
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))

        controller.updateTodayPosition(10)
        controller.onCreate()

        verify(view).scrollToPosition(10)
        verify(view, times(3)).showMonthName(any())
    }

    @Test
    fun shouldScrollToCorrectPositionAndReallyChangeMonthOnTodayWhenMonthIsNotCurrent() {
        stubServiceToReturnEmptyList()
        stubCurrentTime(2017, 2, 10)
        whenever(actions.monthChangeToPrev()).thenReturn(Observable.just(Unit, Unit))
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))

        controller.updateTodayPosition(10)
        controller.onCreate()

        verify(view).scrollToPosition(10)
        verify(view, times(4)).showMonthName(any())
    }

    @Test
    fun shouldListenForFilterActions() {
        stubServiceToReturnEmptyList()

        controller.onCreate()

        verify(actions).reportsFilter()
    }

    @Test
    fun shouldUseFilterActionBeforeShowingReportsList() {
        stubServiceToReturnEmptyList()
        whenever(actions.reportsFilter()).thenReturn(Observable.never())

        controller.onCreate()

        verify(view, never()).showDays(any(), any(), any())
    }

    @Test
    fun shouldDoNotFilterDaysWhenFilterIsOff() {
        stubServiceToReturnEmptyList()
        stubFilterAction(false)

        controller.onCreate()

        verify(filter, never()).fetchFilteredDays(any())
    }


    @Test
    fun shouldCallShowDaysTwiceWhenFilterIsChanged() {
        stubServiceToReturnEmptyList()
        whenever(actions.reportsFilter()).thenReturn(Observable.just(false, true))

        controller.onCreate()

        verify(view, times(2)).showDays(any(), any(), any())
    }

    @Test
    fun shouldFilterDaysWhenFilterIsOn() {
        stubServiceToReturnEmptyList()
        stubFilterAction(true)

        controller.onCreate()

        verify(filter).fetchFilteredDays(any())
    }

    private fun stubViewActions() {
        whenever(actions.reportsFilter()).thenReturn(Observable.just(false))
        whenever(actions.refreshingEvents()).thenReturn(Observable.never())
        whenever(actions.reportAdd()).thenReturn(Observable.never())
        whenever(actions.monthChangeToNext()).thenReturn(Observable.never())
        whenever(actions.monthChangeToPrev()).thenReturn(Observable.never())
        whenever(actions.scrollToCurrent()).thenReturn(Observable.never())
    }

    private fun stubFilterAction(isFiltering: Boolean) {
        whenever(actions.reportsFilter()).thenReturn(Observable.just(isFiltering))
    }

    private fun stubServiceToReturnNever() {
        whenever(service.createDays(any())).thenReturn(Observable.never())
    }

    private fun stubServiceToReturnEmptyList() {
        whenever(service.createDays(any())).thenReturn(Observable.just(listOf()))
    }

    private fun stubServiceToReturnError() {
        whenever(service.createDays(any())).thenReturn(Observable.error(RuntimeException()))
    }

    private fun stubDateChangeToReturn(cal: Calendar) {
        CurrentTimeProvider.override = { cal.timeInMillis }
    }

}
