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
    val controller = ActivitiesController(api, view)

    @Test
    fun shouldDisplay31DaysWithoutReportsIfIsOctoberAndApiReturnsEmptyListOnCreate() {
        verifyIfShowCorrectListForGivenParams(
                apiReturnValue = emptyList<Report>(),
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
        CurrentTimeProvider.override = { dateInMillis(month = month) }
        val days = ArrayList<Day>().apply {
            (1..daysInMonth).forEach { add(Day(it)) }
        }
        stubApiToReturn(apiReturnValue)
        controller.onCreate()
        verify(view, times(1)).showActivities(days)
    }

    private fun stubApiToReturn(list: List<Report>) {
        whenever(api.getActivities()).thenReturn(list)
    }

    private fun dateInMillis(year: Int = 2016, month: Int = 6, day: Int = 1) = Calendar.getInstance().run {
        set(year, month - 1, day)
        timeInMillis
    }

}

data class Day(val dayNumber: Int)

object CurrentTimeProvider : Provider<Long>({ throw NotImplementedError() })

interface ReportList {

    interface Api {
        fun getActivities(): List<Report>
    }

    interface View {
        fun showActivities(reports: ArrayList<Day>)
    }

}

class Report()

class ActivitiesController(val api: ReportList.Api, val view: ReportList.View) {
    fun onCreate() {
        val days = ArrayList<Day>()
        (1..daysForCurrentMonth()).forEach { days.add(Day(it)) }
        view.showActivities(days)
    }

    private fun daysForCurrentMonth() = Calendar.getInstance().run {
        time = Date(CurrentTimeProvider.get())
        getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}
