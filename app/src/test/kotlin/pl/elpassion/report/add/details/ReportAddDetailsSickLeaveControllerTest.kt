package pl.elpassion.report.add.details

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ReportAddDetailsSickLeaveControllerTest {

    @Test
    fun shouldCallApiOnReportAdd() {
        val api = mock<ReportAddDetails.Sender.SickLeave>()
        val controller = ReportAddDetailsSickLeaveController(api)
        controller.onReportAdded()

        verify(api).reportSickLeave()
    }
}