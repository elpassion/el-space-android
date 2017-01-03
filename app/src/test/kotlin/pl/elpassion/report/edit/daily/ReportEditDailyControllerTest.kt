package pl.elpassion.report.edit.daily

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.report.edit.ReportEdit

class ReportEditDailyControllerTest {

    private val view = mock<ReportEdit.Daily.View>()
    private val controller = ReportEditDailyController(view)

    @Test
    fun shouldShowSelectedDateOnCreate() {
        val report = newDailyReport()
        val performedDate = getPerformedAtString(report.year, report.month, report.day)
        controller.onCreate(report)

        verify(view).showDate(performedDate)
    }
}