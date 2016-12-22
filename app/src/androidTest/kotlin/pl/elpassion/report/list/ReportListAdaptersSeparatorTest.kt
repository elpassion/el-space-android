package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.project.dto.newHourlyReport
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

    private fun createReportItem() = ReportItemAdapter(newHourlyReport(), mock())

    private fun createDayWithHourlyReportItem() = DayItemAdapter(newDayWithHourlyReports(), mock())

    private fun createDayWithDailyReportItem() = DayWithDailyReportsItemAdapter(newDayWithDailyReports())

    private fun createNotFilledInDayItem() = DayNotFilledInItemAdapter(newDayWithoutReports(), mock())

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newHourlyReport()), false, 1.0)

    private fun newDayWithDailyReports() = DayWithDailyReport(0, "", "", false, newDailyReport())

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, false)
}