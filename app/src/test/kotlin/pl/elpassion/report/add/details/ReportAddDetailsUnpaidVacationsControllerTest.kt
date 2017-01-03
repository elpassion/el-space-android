package pl.elpassion.report.add.details

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ReportAddDetailsUnpaidVacationsControllerTest {

    @Test
    fun shouldCallApiOnAddReport() {
        val api = mock<ReportAddDetails.Sender.SickLeave>()
        val controller = ReportAddDetailsSickLeaveController(api)
        controller.onReportAdded()

        verify(api).reportSickLeave()
    }
}