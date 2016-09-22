package pl.elpassion.report

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.common.Provider
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

    private fun verifyIfShowCorrectListForGivenParams(apiReturnValue: List<Report>, daysInMonth: Int, month: Int) {
        val days = (1..daysInMonth).map { Day(it) }
        stubApiToReturn(apiReturnValue)
        stubCurrentTime(month = month)
        controller.onCreate()
        verify(view, times(1)).showDays(days)
    }

    private fun stubApiToReturn(list: List<Report>) {
        whenever(api.getReports()).thenReturn(list)
    }

    private fun stubCurrentTime(year: Int = 2016, month: Int = 6, day: Int = 1) {
        CurrentTimeProvider.override = {
            Calendar.getInstance().apply { set(year, month - 1, day) }.timeInMillis
        }
    }
}

data class Day(val dayNumber: Int)

object CurrentTimeProvider : Provider<Long>({ throw NotImplementedError() })

interface ReportList {

    interface Api {
        fun getReports(): List<Report>
    }

    interface View {
        fun showDays(reports: List<Day>)
    }

}

class Report()

class ReportListController(val api: ReportList.Api, val view: ReportList.View) {
    fun onCreate() {
        val days = ArrayList<Day>()
        (1..daysForCurrentMonth()).forEach { days.add(Day(it)) }
        view.showDays(days)
    }

    private fun daysForCurrentMonth() = Calendar.getInstance().run {
        time = Date(CurrentTimeProvider.get())
        getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}
