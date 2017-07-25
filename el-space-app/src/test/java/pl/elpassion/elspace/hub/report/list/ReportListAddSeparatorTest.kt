package pl.elpassion.elspace.hub.report.list

import org.junit.Assert
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport


class ReportListAddSeparatorTest {

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenAdapters = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        print(givenAdapters)
        Assert.assertFalse(givenAdapters[1] is Separator)
    }


}