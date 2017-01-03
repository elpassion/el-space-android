package pl.elpassion.report.edit.daily

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.report.edit.ReportEdit

class ReportEditDailyControllerTest {

    private val view = mock<ReportEdit.Daily.View>()
    private val controller = ReportEditDailyController(view)

    @Test
    fun shouldShowSelectedDateOnCreate() {
        val report = newDailyReport()
        controller.onCreate(report)

        verify(view).showDate(report.date)
    }
}