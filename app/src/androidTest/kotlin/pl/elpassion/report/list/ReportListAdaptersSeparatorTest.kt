package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.project.dto.newHourlyReport
import pl.elpassion.report.list.adapter.addSeparators
import pl.elpassion.report.list.adapter.items.DayItemAdapter
import pl.elpassion.report.list.adapter.items.DayNotFilledInItemAdapter
import pl.elpassion.report.list.adapter.items.ReportItemAdapter
import pl.elpassion.report.list.adapter.items.SeparatorItemAdapter

class ReportListAdaptersSeparatorTest {

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenAdapters = addSeparators(listOf(createReportItem(), createReportItem()))
        assertFalse(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenNotFilledInDayAndFilledIn() {
        val givenAdapters = addSeparators(listOf(createNotFilledInDayItem(), createDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenFilledInDayAndNotFilledIn() {
        val givenAdapters = addSeparators(listOf(createDayItem(), createNotFilledInDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndFilledInDay() {
        val givenAdapters = addSeparators(listOf(createReportItem(), createDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndNotFilledInDay() {
        val givenAdapters = addSeparators(listOf(createReportItem(), createNotFilledInDayItem()))
        assertTrue(givenAdapters[1] is SeparatorItemAdapter)
    }

    private fun createReportItem() = ReportItemAdapter(newHourlyReport(), mock())

    private fun createDayItem() = DayItemAdapter(newDayWithHourlyReports(), mock())

    private fun createNotFilledInDayItem() = DayNotFilledInItemAdapter(newDayWithoutReports(), mock())

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newHourlyReport()), false, 1.0)

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, false)
}