package pl.elpassion.report.edit

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report
import rx.Observable
import rx.Subscription
import kotlin.properties.Delegates

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val editReportApi = mock<ReportEdit.EditApi>()
    private val controller = ReportEditController(view, editReportApi)

    @Before
    fun setUp() {
        whenever(editReportApi.editReport(any(), any(), any(), any(), any())).thenReturn(Observable.just(null))
    }

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
        verify(editReportApi, times(1)).editReport(id = 2, date = "2017-07-02", reportedHour = 8.0, description = "description", projectId = "2")
    }

    @Test
    fun shouldReallyCallApiWithCorrectDataOnSaveReport() {
        controller.onCreate(newReport(year = 2016, month = 1, day = 3, id = 5, description = "DESCRIPTION", reportedHours = 4.0, projectId = 2))
        controller.onSaveReport(hours = 7.5, description = "newDescription")
        verify(editReportApi, times(1)).editReport(id = 5, date = "2016-01-03", reportedHour = 7.5, description = "newDescription", projectId = "2")
    }

    @Test
    fun shouldCallApiWithCorrectProjectIdIfItHasBeenChanged() {
        controller.onCreate(newReport(projectId = 10))
        controller.onSelectProject(newProject(id = "20"))
        controller.onSaveReport(0.0, "")
        verify(editReportApi, times(1)).editReport(any(), any(), any(), any(), projectId = eq("20"))
    }

    @Test
    fun shouldUpdateProjectNameOnNewProject() {
        controller.onSelectProject(newProject(name = "newProject"))
        verify(view, times(1)).updateProjectName(projectName = "newProject")
    }

    @Test
    fun shouldShowLoaderOnSaveReport() {
        controller.onCreate(newReport())
        controller.onSaveReport(1.0, "")
        verify(view, times(1)).showLoader()
    }

    @Test
    fun shouldHideLoaderOnSaveReportFinish() {
        controller.onCreate(newReport())
        controller.onSaveReport(1.0, "")
        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSavingHasNotFinished() {
        whenever(editReportApi.editReport(any(), any(), any(), any(), any())).thenReturn(Observable.never())
        controller.onCreate(newReport())
        controller.onSaveReport(1.0, "")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSavingHasNotFinished() {
        whenever(editReportApi.editReport(any(), any(), any(), any(), any())).thenReturn(Observable.never())
        controller.onCreate(newReport())
        controller.onSaveReport(1.0, "")
        controller.onDestroy()
        verify(view, times(1)).hideLoader()
    }

}

class ReportEditController(val view: ReportEdit.View, val editReportApi: ReportEdit.EditApi) {

    private var reportId: Long by Delegates.notNull()
    private lateinit var reportDate: String
    private lateinit var projectId: String
    private var subscription: Subscription? = null

    fun onCreate(report: Report) {
        reportId = report.id
        reportDate = String.format("%d-%02d-%02d", report.year, report.month, report.day)
        projectId = "${report.projectId}"
        view.showReport(report)
    }

    fun onChooseProject() {
        view.openChooseProjectScreen()
    }

    fun onSaveReport(hours: Double, description: String) {
        subscription = editReportApi.editReport(id = reportId, date = reportDate, reportedHour = hours, description = description, projectId = projectId)
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe()
    }

    fun onSelectProject(project: Project) {
        projectId = project.id
        view.updateProjectName(project.name)
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

}

interface ReportEdit {
    interface View {
        fun showReport(report: Report)
        fun openChooseProjectScreen()
        fun updateProjectName(projectName: String)
        fun showLoader()
        fun hideLoader()
    }

    interface EditApi {
        fun editReport(id: Long, date: String, reportedHour: Double, description: String, projectId: String): Observable<Unit>
    }
}