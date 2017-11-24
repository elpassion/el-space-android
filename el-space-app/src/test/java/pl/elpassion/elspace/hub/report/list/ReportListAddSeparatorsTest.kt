package pl.elpassion.elspace.hub.report.list

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.list.adapter.Empty
import pl.elpassion.elspace.hub.report.list.adapter.Separator
import pl.elpassion.elspace.hub.report.list.adapter.addSeparators


class ReportListAddSeparatorsTest {

    @Test
    fun shouldHaveEmptyOnFirstPosition() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport()))
        assertTrue(givenItems[0] is Empty)
    }

    @Test
    fun shouldHaveEmptyOnLastPosition() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport()))
        assertTrue(givenItems[2] is Empty)
    }

    @Test
    fun shouldHaveCorrectListSizeForOneItem() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport()))
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItems() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveCorrectListSizeForTwoItemsWithoutSeparator() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertTrue(givenItems.size == 4)
    }

    @Test
    fun shouldHaveSeparatorBetweenNotFilledInDayAndFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithHourlyReports()))
        assertTrue(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveCorrectListSizeForTwoItemsWithSeparator() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithHourlyReports()))
        assertTrue(givenItems.size == 5)
    }

    @Test
    fun shouldHaveSeparatorBetweenFilledInDayAndNotFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithHourlyReports(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndFilledInDay() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWithHourlyReports()))
        assertTrue(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndNotFilledInDay() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenDayWithDailyReportAndNotFilledInDay() {
        val givenItems = addSeparators(listOf(newDayWithDailyReports(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenDaysWithoutReports() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenPaidVacationAndRegularHourlyReport() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newPaidVacationHourlyReport()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenWeekendAndEmptyItems() {
        val givenItems = addSeparators(listOf(newDayWeekend(), Empty()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenReportItemAndWeekend() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWeekend()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenDayWithDailyReportAndWeekend() {
        val givenItems = addSeparators(listOf(newDayWithDailyReports(), newDayWeekend()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenNotFilledInDayAndWeekend() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWeekend()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenFilledInDayAndWeekend() {
        val givenItems = addSeparators(listOf(newDayWithHourlyReports(), newDayWeekend()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenPaidVacationAndWeekend() {
        val givenItems = addSeparators(listOf(newPaidVacationHourlyReport(), newDayWeekend()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenTwoWeekendItems() {
        val givenItems = addSeparators(listOf(newDayWeekend(), newDayWeekend()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveCorrectListSizeForTwoWeekendItems() {
        val givenItems = addSeparators(listOf(newDayWeekend(), newDayWeekend()))
        assertTrue(givenItems.size == 4)
    }

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, isWeekend = false)

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newRegularHourlyReport()), false, 1.0)

    private fun newDayWithDailyReports() = DayWithDailyReport(0, "", "", false, newDailyReport())

    private fun newDayWeekend() = DayWithoutReports(0, "", "", false, isWeekend = true)
}