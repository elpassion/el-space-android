package pl.elpassion.elspace.hub.report.list

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.HourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.service.ReportDayServiceImpl
import java.util.*

class ReportDayServiceTest {

    val serviceApi = mock<ReportList.Service>()
    var currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
    var service = ReportDayServiceImpl(serviceApi, { currentTime })

    @Test
    fun shouldCreate31DaysWithoutReportsIfIsOctoberAndApiReturnsEmptyList() {
        verifyIfMapCorrectListForGivenParams(
                apiReturnValue = emptyList(),
                month = 10,
                daysInMonth = 31)
    }

    @Test
    fun shouldCreate30DaysWithoutReportsIfIsNovemberAndApiReturnsEmptyList() {
        verifyIfMapCorrectListForGivenParams(
                apiReturnValue = emptyList(),
                month = 11,
                daysInMonth = 30
        )
    }

    @Test
    fun shouldCorrectlyMapDayName() {
        currentTime = getTimeFrom(year = 2016, month = Calendar.SEPTEMBER, day = 1)
        stubServiceToReturn(emptyList())
        assertEquals(getFirstDay().name, "1 Thu")
    }

    @Test
    fun shouldReallyCorrectlyMapDayName() {
        currentTime = getTimeFrom(year = 2016, month = Calendar.SEPTEMBER, day = 1)
        stubServiceToReturn(emptyList())
        assertEquals(getDays()[1].name, "2 Fri")
    }

    @Test
    fun shouldMarkUnreportedPassedDays() {
        currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 2)
        stubServiceToReturn(emptyList())
        assertTrue(getFirstDay().hasPassed)
    }

    @Test
    fun shouldMapReturnedHourlyReportsToDaysWithHourlyReports() {
        val report = newRegularHourlyReport(year = 2016, month = 6, day = 1)
        currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
        stubServiceToReturn(listOf(report))
        assertTrue(getFirstDay() is DayWithHourlyReports)
        assertEquals((getFirstDay() as DayWithHourlyReports).reports, listOf(report))
    }

    @Test
    fun shouldUnreportedPassedDaysWhichAreNotWeekendsHaveReports() {
        currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 2)
        stubServiceToReturn(emptyList())
        assertTrue(getFirstDay() is DayWithoutReports)
        assertTrue((getFirstDay() as DayWithoutReports).shouldHaveReports())
    }

    @Test
    fun shouldMapReturnedDailyReportsToDaysWithDailyReports() {
        val report = newDailyReport(year = 2016, month = 6, day = 1)
        currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
        stubServiceToReturn(listOf(report))
        assertTrue(getFirstDay() is DayWithDailyReport)
        assertEquals((getFirstDay() as DayWithDailyReport).report, report)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowIllegalArgumentExceptionWhenDayHasDailyReportTogetherWithHourlyReport() {
        val dailyReport = newDailyReport(year = 2016, month = 6, day = 1)
        val hourlyReport = newRegularHourlyReport(year = 2016, month = 6, day = 1)
        currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
        stubServiceToReturn(listOf(dailyReport, hourlyReport))
        getFirstDay()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowIllegalArgumentExceptionWhenDayHasTwoDailyReports() {
        val dailyReport = newDailyReport(year = 2016, month = 6, day = 1)
        currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
        stubServiceToReturn(listOf(dailyReport, dailyReport))
        getFirstDay()
    }

    @Test
    fun shouldCallServiceWithCorrectYearMonth() {
        val yearMonth = currentTime.toYearMonth()
        stubServiceToReturn(emptyList())
        getDays(yearMonth)
        verify(serviceApi).getReports(yearMonth)
    }

    @Test
    fun shouldReallyCallServiceWithCorrectYearMonth() {
        val yearMonth = currentTime.toYearMonth().copy(year = 2015)
        stubServiceToReturn(emptyList())
        getDays(yearMonth)
        verify(serviceApi).getReports(yearMonth)
    }

    private fun getDays(yearMonth: YearMonth = createYearMonthFromTimeProvider()): List<Day> {
        return service.createDays(yearMonth).map { it.filter { it is Day }.map { it as Day } }.blockingFirst()
    }

    private fun getFirstDay() = getDays().first()

    private fun createYearMonthFromTimeProvider() = currentTime.toYearMonth()

    private fun stubServiceToReturn(list: List<Report>) {
        whenever(serviceApi.getReports(any())).thenReturn(Observable.just(list))
    }

    private fun verifyIfMapCorrectListForGivenParams(apiReturnValue: List<HourlyReport>, daysInMonth: Int, month: Int) {
        stubServiceToReturn(apiReturnValue)
        currentTime = getTimeFrom(year = 2016, month = month - 1, day = 1)
        assertEquals(getDays().size, daysInMonth)
    }
}