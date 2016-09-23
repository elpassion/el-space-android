package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import pl.elpassion.common.CurrentTimeProvider
import rx.Observable
import java.util.*

class ReportListControllerTest {

    val api = mock<ReportList.Api>()
    val view = mock<ReportList.View>()
    val controller = ReportListController(api, view)

    @Before
    fun setUp() {
        stubCurrentTime()
    }

    @Test
    fun shouldDisplay31DaysWithoutReportsIfIsOctoberAndApiReturnsEmptyListOnCreate() {
        verifyIfShowCorrectListForGivenParams(
                apiReturnValue = emptyList(),
                month = 10,
                daysInMonth = 31)
    }

    @Test
    fun shouldDisplay30DaysWithoutReportsIfIsNovemberAndApiReturnsEmptyListOnCreate() {
        verifyIfShowCorrectListForGivenParams(
                apiReturnValue = emptyList(),
                month = 11,
                daysInMonth = 30
        )
    }

    @Test
    fun shouldShowCorrectMonthOnCreate() {
        stubApiToReturn(emptyList())
        stubCurrentTime(month = 10)

        controller.onCreate()

        verify(view, times(1)).showMonth("October")
    }

    @Test
    fun shouldReallyShowCorrectMonthName() {
        stubApiToReturn(emptyList())
        stubCurrentTime(month = 11)

        controller.onCreate()

        verify(view, times(1)).showMonth("November")
    }

    @Test
    fun shouldMapReturnedReportsToCorrectDays() {
        val report = Report(2016, 6, 1)
        stubCurrentTime(year = 2016, month = 6, day = 1)
        stubApiToReturn(listOf(report))

        controller.onCreate()

        verify(view, times(1)).showDays(daysWithReportInFirstDayFromCurrentMonth(report))
    }

    @Test
    fun shouldShowErrorWhenApiCallFails() {
        stubApiToReturnError()

        controller.onCreate()

        verify(view, times(1)).showError()
    }

    @Test
    fun shouldShowLoaderWhenApiCallBegins() {
        stubApiToReturnNever()

        controller.onCreate()

        verify(view, times(1)).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenCallIsNotFinishedOnDestroy() {
        stubApiToReturnNever()

        controller.onCreate()
        controller.onDestroy()

        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallFinishes() {
        stubApiToReturn(emptyList())

        controller.onCreate()

        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyWhenCallFinished() {
        stubApiToReturn(emptyList())

        controller.onCreate()
        reset(view)
        controller.onDestroy()

        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldReturnCorrectDaysWhenUserChangeMonthToNext() {
        val report = Report(2016, 6, 1)
        val days = daysWithReportInFirstDayFromNextMonth(report)
        stubCurrentTime(year = 2016, month = 5, day = 1)
        stubApiToReturn(listOf(report))

        controller.onCreate()
        reset(view)
        controller.onNextMonth()

        verify(view, times(1)).showDays(days)
    }

    @Test
    fun shouldReturnCorrectDaysWhenUserChangeMonthToPrevious() {
        val report = Report(2016, 6, 1)
        val days = daysWithReportInFirstDayFromPreviousMonth(report)
        stubCurrentTime(year = 2016, month = 7, day = 1)
        stubApiToReturn(listOf(report))

        controller.onCreate()
        reset(view)
        controller.onPreviousMonth()

        verify(view, times(1)).showDays(days)
    }

    @Test
    fun shouldMarkUnreportedPassedDays() {
        stubCurrentTime(month = 6, day = 1)
        stubApiToReturn(emptyList())

        controller.onCreate()
        val days = listOf(Day(1, emptyList(), true)) + (2..30).map { Day(it, emptyList(), false) }
        verify(view, times(1)).showDays(days)
    }

    @Test
    fun shouldOpenAddReportScreenOnDay() {
        stubApiToReturn(emptyList())
        stubCurrentTime(year = 1999, month = 1)

        controller.onCreate()
        controller.onDay(2)

        verify(view, times(1)).openAddReportScreen("1999-01-02")
    }

    private fun stubApiToReturnNever() {
        whenever(api.getReports()).thenReturn(Observable.never())
    }

    private fun stubApiToReturnError() {
        whenever(api.getReports()).thenReturn(Observable.error(RuntimeException()))
    }

    private fun stubApiToReturn(list: List<Report>) {
        whenever(api.getReports()).thenReturn(Observable.just(list))
    }

    private fun stubCurrentTime(year: Int = 2016, month: Int = 6, day: Int = 1) {
        CurrentTimeProvider.override = {
            Calendar.getInstance().apply { set(year, month - 1, day, 12, 0) }.timeInMillis
        }
    }

    private fun verifyIfShowCorrectListForGivenParams(apiReturnValue: List<Report>, daysInMonth: Int, month: Int) {
        val days = listOf(Day(1, emptyList(), true)) + (2..daysInMonth).map { Day(it, emptyList(), false) }
        stubApiToReturn(apiReturnValue)
        stubCurrentTime(month = month)
        controller.onCreate()
        verify(view, times(1)).showDays(days)
    }

    private fun daysWithReportInFirstDayFromCurrentMonth(report: Report) = listOf(Day(1, listOf(report), true)) + (2..30).map { Day(it, emptyList(), false) }
    private fun daysWithReportInFirstDayFromNextMonth(report: Report) = listOf(Day(1, listOf(report), false)) + (2..30).map { Day(it, emptyList(), false) }
    private fun daysWithReportInFirstDayFromPreviousMonth(report: Report) = listOf(Day(1, listOf(report), true)) + (2..30).map { Day(it, emptyList(), true) }

}
