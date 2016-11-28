package pl.elpassion.report.edit

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report

class ReportEditControllerTest {

    @Test
    fun shouldShowCorrectReportOnCreate() {
        val view = mock<ReportEdit.View>()
        val report = newReport()
        ReportEditController(view).onCreate(report)
        verify(view, times(1)).showReport(report)
    }

    @Test
    fun shouldOpenChooseProjectScreenOnChooseProject() {
        val view = mock<ReportEdit.View>()
        ReportEditController(view).onChooseProject()
        verify(view, times(1)).openChooseProjectScreen()
    }

}

class ReportEditController(val view: ReportEdit.View) {

    fun onCreate(report: Report) {
        view.showReport(report)
    }

    fun onChooseProject() {
        view.openChooseProjectScreen()
    }
}

interface ReportEdit {
    interface View {
        fun showReport(report: Report)
        fun openChooseProjectScreen()
    }
}