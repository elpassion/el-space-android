package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import pl.elpassion.common.CurrentTimeProvider
import rx.Observable
import java.util.*

class ReportListControllerTest {

    val api = mock<ReportList.Api>()
    val view = mock<ReportList.View>()
    val controller = ReportListController(api, view)

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
    fun shouldMapReturnedReportsToCorrectDays() {
        val report = Report(2016, 6, 1)
        stubCurrentTime(year = 2016, month = 6, day = 1)
        stubApiToReturn(listOf(report))

        controller.onCreate()

        verify(view, times(1)).showDays(daysWithReportInFirstDay(report))
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

    private fun stubApiToReturnNever() {
        whenever(api.getReports()).thenReturn(Observable.never())
    }

    private fun stubApiToReturnError() {
        whenever(api.getReports()).thenReturn(Observable.error(RuntimeException()))
    }

    private fun verifyIfShowCorrectListForGivenParams(apiReturnValue: List<Report>, daysInMonth: Int, month: Int) {
        val days = (1..daysInMonth).map { Day(it, emptyList()) }
        stubApiToReturn(apiReturnValue)
        stubCurrentTime(month = month)
        controller.onCreate()
        verify(view, times(1)).showDays(days)
    }

    private fun stubApiToReturn(list: List<Report>) {
        whenever(api.getReports()).thenReturn(Observable.just(list))
    }

    private fun stubCurrentTime(year: Int = 2016, month: Int = 6, day: Int = 1) {
        CurrentTimeProvider.override = {
            Calendar.getInstance().apply { set(year, month - 1, day) }.timeInMillis
        }
    }

    private fun daysWithReportInFirstDay(report: Report) = listOf(Day(1, listOf(report))) + (2..30).map { Day(it, emptyList()) }

}
