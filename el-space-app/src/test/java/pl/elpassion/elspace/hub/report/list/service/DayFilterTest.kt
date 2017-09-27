package pl.elpassion.elspace.hub.report.list.service


import org.junit.Assert.assertEquals
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDayWithoutReports
import pl.elpassion.elspace.hub.report.list.Day
import pl.elpassion.elspace.hub.report.list.DayWithHourlyReports

class DayFilterTest {

    @Test
    fun shouldFilterWeekendPassedDay() {
        val pastWeekendDay = newDayWithoutReports(isWeekend = true, hasPassed = true)

        assertEquals(filterDays(pastWeekendDay), emptyList<Day>())
    }

    @Test
    fun shouldFilterWeekendNotPassedDay() {
        val futureWeekendDay = newDayWithoutReports(isWeekend = true, hasPassed = false)

        assertEquals(filterDays(futureWeekendDay), emptyList<Day>())
    }

    @Test
    fun shouldFilterNotPassedDay() {
        val futureDay = newDayWithoutReports(isWeekend = false, hasPassed = false)

        assertEquals(filterDays(futureDay), emptyList<Day>())
    }

    @Test
    fun shouldNotFilterPassedNormalDay() {
        val pastDay = newDayWithoutReports(isWeekend = false, hasPassed = true)

        assertEquals(filterDays(pastDay), listOf(pastDay))
    }

    @Test
    fun shouldFilterReportedDayWithHours() {
        val futureDay = newDayReports()

        assertEquals(filterDays(futureDay), emptyList<Day>())
    }

    private fun filterDays(vararg days: Day) = DayFilterImpl().fetchFilteredDays(days.toList())

    private fun newDayReports() = DayWithHourlyReports(0, "", "", listOf(), false, 2.00)

}