package pl.elpassion.report.add.details

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.report.add.details.paidvacations.ReportAddDetailsPaidVacationsController

class ReportPaidVacationsAddControllerTest {

    val view = mock<ReportAddDetails.View.PaidVacations>()
    val sender = mock<ReportAddDetails.Sender.PaidVacations>()
    val controller = ReportAddDetailsPaidVacationsController(view, sender)

    @Test
    fun shouldCallApiOnReportAdd() {
        whenever(view.getHours()).thenReturn("8")
        controller.onReportAdded()
        verify(sender).addPaidVacationsReport("8")
    }

    @Test
    fun shouldReallyCallApiOnReportAdd() {
        whenever(view.getHours()).thenReturn("9")
        controller.onReportAdded()
        verify(sender).addPaidVacationsReport("9")
    }
}