package pl.elpassion.elspace.hub.report.list

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport


class ReportListAddSeparatorTest {

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertFalse(givenItems[1] is Separator)
        assertTrue(givenItems.size == 2)
    }

    @Test
    fun shouldHaveSeparatorBetweenNotFilledInDayAndFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithHourlyReports()))
        assertTrue(givenItems[1] is Separator)
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldHaveSeparatorBetweenFilledInDayAndNotFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithHourlyReports(), newDayWithoutReports()))
        assertTrue(givenItems[1] is Separator)
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndFilledInDay() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWithHourlyReports()))
        assertTrue(givenItems[1] is Separator)
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndNotFilledInDay() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWithoutReports()))
        assertTrue(givenItems[1] is Separator)
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldHaveSeparatorBetweenDayWithDailyReportAndNotFilledInDay() {
        val givenItems = addSeparators(listOf(newDayWithDailyReports(), newDayWithoutReports()))
        assertTrue(givenItems[1] is Separator)
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldHaveSeparatorBetweenDaysWithoutReports() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithoutReports()))
        assertTrue(givenItems[1] is Separator)
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenPaidVacationAndRegularHourlyReport() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newPaidVacationHourlyReport()))
        assertFalse(givenItems[1] is Separator)
        assertTrue(givenItems.size == 2)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenWeekendAndEmptyItems() {
        val givenItems = addSeparators(listOf(newDayWeekend(), Empty()))
        print(givenItems)
        assertFalse(givenItems[1] is Separator)
        assertTrue(givenItems.size == 2)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenTwoWeekendItems() {
        val givenItems = addSeparators(listOf(newDayWeekend(), newDayWeekend()))
        print(givenItems)
        assertFalse(givenItems[1] is Separator)
        assertTrue(givenItems.size == 2)
    }

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, isWeekend = false)

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newRegularHourlyReport()), false, 1.0)

    private fun newDayWithDailyReports() = DayWithDailyReport(0, "", "", false, newDailyReport())

    private fun newDayWeekend() = DayWithoutReports(0, "", "", false, isWeekend = true)
}