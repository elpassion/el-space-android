package pl.elpassion.report

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.common.Provider
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

data class Day(val dayNumber: Int, val reports: List<Report>)

object CurrentTimeProvider : Provider<Long>({ throw NotImplementedError() })

interface ReportList {

    interface Api {
        fun getReports(): Observable<List<Report>>
    }

    interface View {
        fun showDays(reports: List<Day>)

        fun showError()
    }

}

data class Report(val year: Int, val month: Int, val day: Int)

class ReportListController(val api: ReportList.Api, val view: ReportList.View) {
    fun onCreate() {
        api.getReports().subscribe({ reports ->
            val days = ArrayList<Day>()
            (1..daysForCurrentMonth()).forEach { days.add(Day(it, reports.filter(getReportsForDay(it)))) }
            view.showDays(days)
        },{
            view.showError()
        })

    }

    private fun getReportsForDay(day: Int): (Report) -> Boolean {
        return { report ->
            val date = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
            report.year == date.get(Calendar.YEAR) && report.month == date.get(Calendar.MONTH) + 1 && report.day == day
        }
    }

    private fun daysForCurrentMonth() = Calendar.getInstance().run {
        time = Date(CurrentTimeProvider.get())
        getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}
