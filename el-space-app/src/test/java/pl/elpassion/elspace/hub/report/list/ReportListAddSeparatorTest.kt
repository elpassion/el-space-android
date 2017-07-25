package pl.elpassion.elspace.hub.report.list

import org.junit.Assert.assertFalse
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport


class ReportListAddSeparatorTest {

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenAdapters = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertFalse(givenAdapters[1] is Separator)
    }
}