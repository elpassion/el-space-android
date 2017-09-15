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
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.adapter.Empty
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersServiceImpl
import java.util.*

class ReportsListAdaptersServiceTest : TreeSpec() {

    private val serviceApi = mock<ReportList.Service>()
    private var currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1)
    private var service = ReportsListAdaptersServiceImpl(serviceApi, { currentTime })

    init {
        "Report day service should " {
            stubServiceToReturn(emptyList())
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
            "at date: 2016-06-01, " {
                before { currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1) }
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
                        stubServiceToReturn(listOf(dailyReport, dailyReport))
                        getFirstDay()
                    }
                }
                "map returned daily reports to days with daily reports" > {
                    val report = newDailyReport(year = 2016, month = 6, day = 1)
                    stubServiceToReturn(listOf(report))
                    assertTrue(getFirstDay() is DayWithDailyReport)
                    assertEquals((getFirstDay() as DayWithDailyReport).report, report)
                }
                "map returned hourly reports to days with hourly reports" > {
                    val report = newRegularHourlyReport(year = 2016, month = 6, day = 1)
                    stubServiceToReturn(listOf(report))
                    assertTrue(getFirstDay() is DayWithHourlyReports)
                    assertEquals((getFirstDay() as DayWithHourlyReports).reports, listOf(report))
                }
                "correctly map day name" > {
                    assertEquals("1 Wed", getFirstDay().name)
                }
                "really correctly map day name" > {
                    assertEquals("2 Thu", getDays()[1].name)
                }

            }
            "at date: 2016-06-02, " {
                before { currentTime = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 2) }
                "mark unreported passed days" > {
                    assertTrue(getFirstDay().hasPassed)
                }
                "unreported passed days which are not weekends have reports" > {
                    assertTrue(getFirstDay() is DayWithoutReports)
                    assertTrue((getFirstDay() as DayWithoutReports).shouldHaveReports())
                }
            }
            "call service with correct yearMonth" > {
                val yearMonth = currentTime.toYearMonth()
                getDays(yearMonth)
                verify(serviceApi).getReports(yearMonth)
            }
            "really call service with correct yearMonth" > {
                val yearMonth = currentTime.toYearMonth().copy(year = 2015)
                getDays(yearMonth)
                verify(serviceApi).getReports(yearMonth)
            }
            "add reports items" > {
                whenever(serviceApi.getReports(any())).thenReturn(Observable.just(listOf(newRegularHourlyReport(year = 2016, month = 6, day = 1))))
                service.createReportsListAdapters(currentTime.toYearMonth())
                        .map { it.filter { it is Day || it is Report } }
                        .test()
                        .assertValue { it.first() is DayWithHourlyReports }
                        .assertValue { it[1] is RegularHourlyReport }
            }
            "add separators to list" > {
                service.createReportsListAdapters(currentTime.toYearMonth())
                        .test()
                        .assertValue { it.first() is Empty }
                        .assertValue { it.last() is Empty }
            }
        }
    }

    private fun getDays(yearMonth: YearMonth = createYearMonthFromTimeProvider()): List<Day> {
        return service.createReportsListAdapters(yearMonth).map { it.filter { it is Day }.map { it as Day } }.blockingFirst()
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