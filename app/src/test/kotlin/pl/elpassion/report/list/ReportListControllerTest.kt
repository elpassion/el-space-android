package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report
import rx.Observable

class ReportListControllerTest {

    val service = mock<ReportList.Service>()
    val view = mock<ReportList.View>()
    val controller = ReportListController(service, view)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

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
        stubServiceToReturn(emptyList())
        stubCurrentTime(month = 10)

        controller.onCreate()

        verify(view, times(1)).showMonthName("October")
    }

    @Test
    fun shouldReallyShowCorrectMonthName() {
        stubServiceToReturn(emptyList())
        stubCurrentTime(month = 11)

        controller.onCreate()

        verify(view, times(1)).showMonthName("November")
    }

    @Test
    fun shouldMapReturnedReportsToCorrectDays() {
        val report = newReport(year = 2016, month = 6, day = 1)
        stubCurrentTime(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(report))

        controller.onCreate()

        verify(view, times(1)).showDays(argThat { this[0].reports == listOf(report) }, any())
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
    fun shouldHideLoaderWhenApiCallFinishes() {
        stubServiceToReturn(emptyList())

        controller.onCreate()

        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyWhenCallFinished() {
        stubServiceToReturn(emptyList())

        controller.onCreate()
        reset(view)
        controller.onDestroy()

        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldReturnCorrectDaysWhenUserChangeMonthToNext() {
        val report = newReport(year = 2016, month = 6, day = 1, reportedHours = 1.0)
        stubCurrentTime(year = 2016, month = 5, day = 1)
        stubServiceToReturn(listOf(report))

        controller.onCreate()
        reset(view)
        controller.onNextMonth()

        verify(view, times(1)).showDays(argThat { this[0].reports == listOf(report) }, any())
    }

    @Test
    fun shouldShowCorrectMonthOnNextMonth() {
        stubCurrentTime(year = 2016, month = 7, day = 1)
        stubServiceToReturn(emptyList())

        controller.onCreate()
        reset(view)
        controller.onNextMonth()

        verify(view, times(1)).showMonthName("August")
    }

    @Test
    fun shouldReturnCorrectDaysWhenUserChangeMonthToPrevious() {
        val report = newReport(year = 2016, month = 6, day = 1)
        stubCurrentTime(year = 2016, month = 7, day = 1)
        stubServiceToReturn(listOf(report))

        controller.onCreate()
        reset(view)
        controller.onPreviousMonth()

        verify(view, times(1)).showDays(argThat { this[0].reports == listOf(report) }, any())
    }

    @Test
    fun shouldShowCorrectMonthOnPreviousMonth() {
        stubCurrentTime(year = 2016, month = 7, day = 1)
        stubServiceToReturn(emptyList())

        controller.onCreate()
        reset(view)
        controller.onPreviousMonth()

        verify(view, times(1)).showMonthName("June")
    }

    @Test
    fun shouldMarkUnreportedPassedDays() {
        stubCurrentTime(month = 6, day = 1)
        stubServiceToReturn(emptyList())

        controller.onCreate()
        verify(view, times(1)).showDays(argThat { this[0].hasPassed }, any())
    }

    @Test
    fun shouldOpenAddReportScreenOnDay() {
        stubCurrentTime(year = 1999, month = 1)

        controller.onDay(2)

        verify(view, times(1)).openAddReportScreen("1999-01-02")
    }

    @Test
    fun shouldOpenEditReportScreenOnReport() {
        val report = newReport()
        controller.onReport(report)

        verify(view, times(1)).openEditReportScreen(report)
    }

    @Test
    fun shouldCorrectlyMapWeekendDays() {
        stubCurrentTime(year = 2016, month = 9)
        stubServiceToReturn(emptyList())
        controller.onCreate()
        reset(view)
        controller.onNextMonth()

        verify(view).showDays(argThat { this[0].isWeekendDay && this[1].isWeekendDay && !this[2].isWeekendDay}, any())
    }

    @Test
    fun shouldCorrectlyMapDayName() {
        stubCurrentTime(year = 2016, month = 9)
        stubServiceToReturn(emptyList())
        controller.onCreate()

        verify(view).showDays(argThat { this[0].name == "1 Thu"}, any())
    }

    @Test
    fun shouldReallyCorrectlyMapDayName() {
        stubCurrentTime(year = 2016, month = 9)
        stubServiceToReturn(emptyList())
        controller.onCreate()

        verify(view).showDays(argThat { this[1].name == "2 Fri"}, any())
    }

    @Test
    fun shouldNotCollectDuplicatedReports() {
        val report = newReport(year = 2016, month = 6, day = 1)
        stubCurrentTime(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(report))

        controller.onCreate()
        reset(view)
        controller.onCreate()

        verify(view, times(1)).showDays(argThat { this[0].reports.size == 1 }, any())
    }

    private fun stubServiceToReturnNever() {
        whenever(service.getReports()).thenReturn(Observable.never())
    }

    private fun stubServiceToReturnError() {
        whenever(service.getReports()).thenReturn(Observable.error(RuntimeException()))
    }

    private fun stubServiceToReturn(list: List<Report>) {
        whenever(service.getReports()).thenReturn(Observable.just(list))
    }

    private fun verifyIfShowCorrectListForGivenParams(apiReturnValue: List<Report>, daysInMonth: Int, month: Int) {
        stubServiceToReturn(apiReturnValue)
        stubCurrentTime(month = month)
        controller.onCreate()
        verify(view, times(1)).showDays(argThat { this.size == daysInMonth }, any())
    }

}
