package pl.elpassion.report.add.details

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.report.add.details.sickleave.ReportAddDetailsSickLeaveController

class ReportAddDetailsUnpaidVacationsControllerTest {

    @Test
    fun shouldCallApiOnAddReport() {
        val api = mock<ReportAddDetails.Sender.SickLeave>()
        val controller = ReportAddDetailsSickLeaveController(api)
        controller.onReportAdded()

        verify(api).addSickLeaveReport()
    }
}