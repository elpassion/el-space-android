package pl.elpassion.elspace.hub.report.list

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport


class ReportListAddSeparatorTest {

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertFalse(givenItems[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenNotFilledInDayAndFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithHourlyReports()))
        assertTrue(givenItems[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenFilledInDayAndNotFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithHourlyReports(), newDayWithoutReports()))
        assertTrue(givenItems[1] is Separator)
    }

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, false)

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newRegularHourlyReport()), false, 1.0)
}