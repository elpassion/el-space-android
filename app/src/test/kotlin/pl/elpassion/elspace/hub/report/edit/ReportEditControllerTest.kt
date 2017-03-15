package pl.elpassion.elspace.hub.report.edit

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()

    @Test
    fun shouldShowReportDateOnCreate() {
        val report = newRegularHourlyReport(year = 2017, month = 1, day = 1)
        ReportEditController(report, view).onCreate()
        verify(view).showDate("2017-01-01")
    }
}