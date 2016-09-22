package pl.elpassion.report

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.joda.time.DateTime
import org.junit.Test
import pl.elpassion.common.Provider
import java.util.*

class ReportListControllerTest {

    @Test
    fun shouldDisplay31DaysWithoutReportsIfIsOctoberAndApiReturnsEmptyListOnCreate() {
        CurrentTimeProvider.override = { dateInMillis(month = 10) }
        val days = ArrayList<Day>()
        (1..31).forEach { days.add(Day(it)) }
        val api = mock<ReportList.Api>()
        whenever(api.getActivities()).thenReturn(emptyList())
        val view = mock<ReportList.View>()
        val controller = ActivitiesController(api, view)
        controller.onCreate()

        verify(view, times(1)).showActivities(days)
    }

    @Test
    fun shouldDisplay30DaysWithoutReportsIfIsNovemberAndApiReturnsEmptyListOnCreate() {
        CurrentTimeProvider.override = { dateInMillis(month = 11) }
        val days = ArrayList<Day>()
        (1..30).forEach { days.add(Day(it)) }
        val api = mock<ReportList.Api>()
        whenever(api.getActivities()).thenReturn(emptyList())
        val view = mock<ReportList.View>()
        val controller = ActivitiesController(api, view)
        controller.onCreate()

        verify(view, times(1)).showActivities(days)
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

    private fun daysForCurrentMonth() = DateTime(CurrentTimeProvider.get()).toGregorianCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)

}
