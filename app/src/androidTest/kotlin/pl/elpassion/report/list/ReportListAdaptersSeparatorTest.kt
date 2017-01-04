package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.project.dto.newPaidVacationHourlyReport
import pl.elpassion.project.dto.newRegularHourlyReport
import pl.elpassion.report.list.adapter.addSeparators
import pl.elpassion.report.list.adapter.items.*

class ReportListAdaptersSeparatorTest {

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenAdapters = addSeparators(listOf(createReportItem(), createReportItem()))
        assertFalse(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenNotFilledInDayAndFilledIn() {
        val givenAdapters = addSeparators(listOf(createNotFilledInDayItem(), createDayWithHourlyReportItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenFilledInDayAndNotFilledIn() {
        val givenAdapters = addSeparators(listOf(createDayWithHourlyReportItem(), createNotFilledInDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndFilledInDay() {
        val givenAdapters = addSeparators(listOf(createReportItem(), createDayWithHourlyReportItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndNotFilledInDay() {
        val givenAdapters = addSeparators(listOf(createReportItem(), createNotFilledInDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenDayWithDailyReportAndNotFilledInDay() {
        val givenAdapters = addSeparators(listOf(createDayWithDailyReportItem(), createNotFilledInDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenDaysWithoutReports() {
        val givenAdapters = addSeparators(listOf(createNotFilledInDayItem(), createNotFilledInDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenTwoWeekendItems() {
        val givenAdapters = addSeparators(listOf(createWeekendDayItem(), createWeekendDayItem()))
        assertFalse(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenWeekendAndEmptyItems() {
        val givenAdapters = addSeparators(listOf(createWeekendDayItem(), createEmptyItem()))
        assertFalse(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenPaidVacationAndRegularHourlyReport() {
        val givenAdapters = addSeparators(listOf(createReportItem(), createPaidVacationReportItem()))
        assertFalse(givenAdapters[1] is SeparatorItemAdapter)
    }

    private fun createReportItem() = RegularReportItemAdapter(newRegularHourlyReport(), mock())

    private fun createDayWithHourlyReportItem() = DayItemAdapter(newDayWithHourlyReports(), mock())

    private fun createDayWithDailyReportItem() = DayWithDailyReportsItemAdapter(newDayWithDailyReports(), mock())

    private fun createNotFilledInDayItem() = DayNotFilledInItemAdapter(newDayWithoutReports(), mock())

    private fun createWeekendDayItem() = WeekendDayItem(newDayWithoutReports(), mock())

    private fun createEmptyItem() = EmptyItemAdapter()

    private fun createPaidVacationReportItem() = PaidVacationReportItemAdapter(newPaidVacationHourlyReport(), mock())

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newRegularHourlyReport()), false, 1.0)

    private fun newDayWithDailyReports() = DayWithDailyReport(0, "", "", false, newDailyReport())

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, false)
}