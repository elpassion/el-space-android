package pl.elpassion.elspace.hub.report.list

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.extensions.getCurrentTimeCalendar
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.HourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.service.ReportDayServiceImpl
import rx.Observable
import rx.Observable.just
import java.util.*

class ReportDayServiceTest {
    val listService = mock<ReportList.Service>()
    val service = ReportDayServiceImpl(listService)

    @Test
    fun shouldCreate31DaysWithoutReportsIfIsOctoberAndServiceReturnsEmptyList() {
        verifyIfMapCorrectListForGivenParams(
                givenReports = emptyList(),
                month = 10,
                daysInMonth = 31)
    }

    @Test
    fun shouldCreate30DaysWithoutReportsIfIsNovemberAndServiceReturnsEmptyList() {
        verifyIfMapCorrectListForGivenParams(
                givenReports = emptyList(),
                month = 11,
                daysInMonth = 30
        )
    }

    @Test
    fun shouldCorrectlyMapDayName() {
        stubDateChangeObserver(year = 2016, month = 9)
        stubServiceToReturn(emptyList())

        assertEquals(getFirstDay().name, "1 Thu")
    }

    @Test
    fun shouldReallyCorrectlyMapDayName() {
        stubDateChangeObserver(year = 2016, month = 9)
        stubServiceToReturn(emptyList())

        assertEquals(getDays()[1].name, "2 Fri")
    }

    @Test
    fun shouldMarkUnreportedPassedDays() {
        stubDateChangeObserver(year = 2016, month = 6)
        stubServiceToReturn(emptyList())

        assertTrue(getFirstDay().hasPassed)
    }

    @Test
    fun shouldMapReturnedHourlyReportsToDaysWithHourlyReports() {
        val report = newRegularHourlyReport(year = 2016, month = 6, day = 1)
        stubDateChangeObserver(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(report))

        assertTrue(getFirstDay() is DayWithHourlyReports)
        assertEquals((getFirstDay() as DayWithHourlyReports).reports, listOf(report))
    }

    @Test
    fun shouldUnreportedPassedDaysWhichAreNotWeekendsHaveReports() {
        stubDateChangeObserver(year = 2016, month = 6)
        stubServiceToReturn(emptyList())

        assertTrue(getFirstDay() is DayWithoutReports)
        assertTrue((getFirstDay() as DayWithoutReports).shouldHaveReports())
    }

    @Test
    fun shouldMapReturnedDailyReportsToDaysWithDailyReports() {
        val report = newDailyReport(year = 2016, month = 6, day = 1)
        stubDateChangeObserver(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(report))

        assertTrue(getFirstDay() is DayWithDailyReport)
        assertEquals((getFirstDay() as DayWithDailyReport).report, report)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowIllegalArgumentExceptionWhenDayHasDailyReportTogetherWithHourlyReport() {
        val dailyReport = newDailyReport(year = 2016, month = 6, day = 1)
        val hourlyReport = newRegularHourlyReport(year = 2016, month = 6, day = 1)
        stubDateChangeObserver(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(dailyReport, hourlyReport))

        getFirstDay()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowIllegalArgumentExceptionWhenDayHasTwoDailyReports() {
        val dailyReport = newDailyReport(year = 2016, month = 6, day = 1)
        stubDateChangeObserver(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(dailyReport, dailyReport))

        getFirstDay()
    }

    @Test
    fun shouldCallServiceWithCorrectYearMonth() {
        val yearMonth = getCurrentTimeCalendar().toYearMonth()
        stubServiceToReturn(emptyList())

        getDays(just(yearMonth))
        verify(listService).getReports(yearMonth)
    }

    @Test
    fun shouldReallyCallServiceWithCorrectYearMonth() {
        val yearMonth = getCurrentTimeCalendar().toYearMonth().copy(year = 2015)
        stubServiceToReturn(emptyList())

        getDays(just(yearMonth))
        verify(listService).getReports(yearMonth)
    }

    private fun getDays(dateChangeObservable: Observable<YearMonth> = createYearMonthFromTimeProvider()): List<Day> {
        return service.createDays(dateChangeObservable).toBlocking().first()
    }

    private fun getFirstDay() = getDays().first()

    private fun createYearMonthFromTimeProvider() =
            Observable.just(Calendar.getInstance().apply { timeInMillis = CurrentTimeProvider.get() }.toYearMonth())

    private fun stubServiceToReturn(list: List<Report>) {
        whenever(listService.getReports(any())).thenReturn(Observable.just(list))
    }

    private fun stubDateChangeObserver(year: Int, month: Int, day: Int = 1) {
        stubCurrentTime(year = year, month = month, day = day)
    }

    private fun verifyIfMapCorrectListForGivenParams(givenReports: List<HourlyReport>, daysInMonth: Int, month: Int) {
        stubServiceToReturn(givenReports)
        stubDateChangeObserver(year = 2016, month = month)
        assertEquals(getDays().size, daysInMonth)
    }
}