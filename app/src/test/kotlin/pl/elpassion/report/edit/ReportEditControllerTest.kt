package pl.elpassion.report.edit

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report
import kotlin.properties.Delegates

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val editReportApi = mock<ReportEdit.EditApi>()
    private val controller = ReportEditController(view, editReportApi)

    @Test
    fun shouldShowCorrectReportOnCreate() {
        val report = newReport()
        controller.onCreate(report)
        verify(view, times(1)).showReport(report)
    }

    @Test
    fun shouldOpenChooseProjectScreenOnChooseProject() {
        controller.onChooseProject()
        verify(view, times(1)).openChooseProjectScreen()
    }

    @Test
    fun shouldCallApiWithCorrectDataOnSaveReport() {
        controller.onCreate(newReport(year = 2017, month = 7, day = 2, id = 2, description = "DESCRIPTION", reportedHours = 4.0, projectId = 2))
        controller.onSaveReport(hours = 8.0, description = "description")
        verify(editReportApi, times(1)).editReport(id = 2, date = "2017-07-02", reportedHour = 8.0, description = "description", projectId = 2)
    }

    @Test
    fun shouldReallyCallApiWithCorrectDataOnSaveReport() {
        controller.onCreate(newReport(year = 2016, month = 1, day = 3, id = 5, description = "DESCRIPTION", reportedHours = 4.0, projectId = 2))
        controller.onSaveReport(hours = 7.5, description = "newDescription")
        verify(editReportApi, times(1)).editReport(id = 5, date = "2016-01-03", reportedHour = 7.5, description = "newDescription", projectId = 2)
    }

}

class ReportEditController(val view: ReportEdit.View, val editReportApi: ReportEdit.EditApi) {

    private var reportId: Long by Delegates.notNull()
    private lateinit var reportDate: String

    fun onCreate(report: Report) {
        reportId = report.id
        reportDate = String.format("%d-%02d-%02d", report.year, report.month, report.day)
        view.showReport(report)
    }

    fun onChooseProject() {
        view.openChooseProjectScreen()
    }

    fun onSaveReport(hours: Double, description: String) {
        editReportApi.editReport(id = reportId, date = reportDate, reportedHour = hours, description = description, projectId = 2)
    }
}

interface ReportEdit {
    interface View {
        fun showReport(report: Report)
        fun openChooseProjectScreen()
    }

    interface EditApi {
        fun editReport(id: Long, date: String, reportedHour: Double, description: String, projectId: Int)
    }
}