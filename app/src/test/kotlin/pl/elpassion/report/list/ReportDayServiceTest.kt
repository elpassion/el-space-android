package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.report.Report
import rx.Observable
import java.util.*

class ReportDayServiceTest {
    val dateChangeObserver = mock<DateChangeObserver>()
    val serviceApi = mock<ReportList.Service>()
    var service = ReportDayServiceImpl(dateChangeObserver, serviceApi)

    @Test
    fun shouldCorrectlyMapDayName() {
        stubDateChangeObserver(year = 2016, month = 9)
        stubServiceToReturn(emptyList())

        assertEquals(getFirstDay().name, "1 Thu")
    }

    @Test
    fun shouldMarkUnreportedPassedDays() {
        stubDateChangeObserver(year = 2016, month = 6)
        stubServiceToReturn(emptyList())

        assertTrue(getFirstDay().hasPassed)
    }

    private fun getDays() = service.createDays().toBlocking().first()

    private fun getFirstDay() = getDays().first()

    private fun stubServiceToReturn(list: List<Report>) {
        whenever(serviceApi.getReports()).thenReturn(Observable.just(list))
    }

    private fun stubDateChangeObserver(year: Int, month: Int) {
        stubCurrentTime(year = year, month = month)
        val initialDateCalendar: Calendar = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
        whenever(dateChangeObserver.observe()).thenReturn(Observable.just(initialDateCalendar.toYearMonth()))
    }
}