package pl.elpassion.elspace.hub.report.list

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.matchers.shouldThrow
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import pl.elpassion.elspace.common.TreeSpec
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.HourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.service.ReportDayServiceImpl
import java.util.*

class ReportDayServiceTest : TreeSpec() {

    private val serviceApi = mock<ReportList.Service>()
    private var currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
    private var service = ReportDayServiceImpl(serviceApi, { currentTime })


    init {
        "Report day service should " {
            "create 31 days without reports if is october and api returns empty list" > {
                verifyIfMapCorrectListForGivenParams(
                        apiReturnValue = emptyList(),
                        month = 10,
                        daysInMonth = 31)
            }
            "create 30 days without reports if is november and api returns empty list" > {
                verifyIfMapCorrectListForGivenParams(
                        apiReturnValue = emptyList(),
                        month = 11,
                        daysInMonth = 30
                )
            }
            "correctly map day name" > {
                currentTime = getTimeFrom(year = 2016, month = Calendar.SEPTEMBER, day = 1)
                stubServiceToReturn(emptyList())
                assertEquals(getFirstDay().name, "1 Thu")
            }
            "really correctly map day name" > {
                currentTime = getTimeFrom(year = 2016, month = Calendar.SEPTEMBER, day = 1)
                stubServiceToReturn(emptyList())
                assertEquals(getDays()[1].name, "2 Fri")
            }
            "mark unreported passed days" > {
                currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 2)
                stubServiceToReturn(emptyList())
                assertTrue(getFirstDay().hasPassed)
            }
            "map returned hourly reports to days with hourly reports" > {
                val report = newRegularHourlyReport(year = 2016, month = 6, day = 1)
                currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
                stubServiceToReturn(listOf(report))
                assertTrue(getFirstDay() is DayWithHourlyReports)
                assertEquals((getFirstDay() as DayWithHourlyReports).reports, listOf(report))
            }
            "unreported passed days wchich are not weekends have reports" > {
                currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 2)
                stubServiceToReturn(emptyList())
                assertTrue(getFirstDay() is DayWithoutReports)
                assertTrue((getFirstDay() as DayWithoutReports).shouldHaveReports())
            }
            "map returned daily reports to days with daily reports" > {
                val report = newDailyReport(year = 2016, month = 6, day = 1)
                currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
                stubServiceToReturn(listOf(report))
                assertTrue(getFirstDay() is DayWithDailyReport)
                assertEquals((getFirstDay() as DayWithDailyReport).report, report)
            }
            "throw IllegalArgumentException when day has daily report together with hourly report" > {
                shouldThrow<IllegalArgumentException> {
                    val dailyReport = newDailyReport(year = 2016, month = 6, day = 1)
                    val hourlyReport = newRegularHourlyReport(year = 2016, month = 6, day = 1)
                    currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
                    stubServiceToReturn(listOf(dailyReport, hourlyReport))
                    getFirstDay()
                }
            }
            "throw IllegalArgumentException when day has two daily reports" > {
                shouldThrow<IllegalArgumentException> {
                    val dailyReport = newDailyReport(year = 2016, month = 6, day = 1)
                    currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
                    stubServiceToReturn(listOf(dailyReport, dailyReport))
                    getFirstDay()
                }
            }
            "call service with correct yearMonth" > {
                val yearMonth = currentTime.toYearMonth()
                stubServiceToReturn(emptyList())
                getDays(yearMonth)
                verify(serviceApi).getReports(yearMonth)
            }
            "really call service with correct yearMonth" > {
                val yearMonth = currentTime.toYearMonth().copy(year = 2015)
                stubServiceToReturn(emptyList())
                getDays(yearMonth)
                verify(serviceApi).getReports(yearMonth)
            }
        }
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