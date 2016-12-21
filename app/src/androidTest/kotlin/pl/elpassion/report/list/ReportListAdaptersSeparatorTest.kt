package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.project.dto.newReport
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

    private fun createWeekendItem() = WeekendDayItem(newDay(), mock())

    private fun createReportItem() = ReportItemAdapter(newReport(), mock())

    private fun createDayItem() = DayItemAdapter(newDay(), mock())

    private fun createNotFilledInDayItem() = DayNotFilledInItemAdapter(newDay(), mock())

    private fun newDay() = Day(0, "", "", emptyList(), false, 1.0, false)
}