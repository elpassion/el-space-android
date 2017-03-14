package pl.elpassion.elspace.hub.report.edit.regular

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.commons.RxSchedulersRule
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import rx.Completable

class ReportEditRegularControllerTest {

    private val view = mock<ReportEdit.Regular.View>()
    private val editReportService = mock<ReportEdit.Regular.Service>()
    private val removeReportApi = mock<ReportEdit.RemoveApi>()
    private val controller = ReportEditRegularController(view, editReportService, removeReportApi)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubEditReportServiceToReturnSuccess()
        stubRemoveReportApiToReturnSuccess()
    }

    @Test
    fun shouldShowCorrectReportOnCreate() {
        val report = newRegularHourlyReport()

        controller.onCreate(report)

        verify(view).showReport(report)
    }

    @Test
    fun shouldOpenChooseProjectScreenOnChooseProject() {
        controller.onChooseProject()

        verify(view).openChooseProjectScreen()
    }

    @Test
    fun shouldCallServiceWithCorrectDataOnSaveReport() {
        val report = newRegularHourlyReport(year = 2017, month = 7, day = 2, id = 2, description = "DESCRIPTION", reportedHours = 4.0, project = newProject(id = 2))
        controller.onCreate(report)

        controller.onSaveReport(hours = "8.0", description = "description")

        verify(editReportService).edit(report.copy(reportedHours = 8.0, description = "description"))
    }

    @Test
    fun shouldReallyCallServiceWithCorrectDataOnSaveReport() {
        val report = newRegularHourlyReport(year = 2016, month = 1, day = 3, id = 5, description = "DESCRIPTION", reportedHours = 4.0, project = newProject(id = 2))
        controller.onCreate(report)

        controller.onSaveReport(hours = "7.5", description = "newDescription")

        verify(editReportService).edit(report.copy(reportedHours = 7.5, description = "newDescription"))
    }

    @Test
    fun shouldShowEmptyDescriptionError() {
        controller.onCreate(newRegularHourlyReport())
        controller.onSaveReport("8", "")

        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldCallServiceWithCorrectProjectIdIfItHasBeenChanged() {
        controller.onCreate(newRegularHourlyReport(project = newProject(id = 10)))
        controller.onSelectProject(newProject(id = 20))
        controller.onSaveReport("0.0", "description")

        verify(editReportService).edit(argThat { project.id == 20L })
    }

    @Test
    fun shouldUpdateProjectNameOnNewProject() {
        controller.onCreate(newRegularHourlyReport())

        controller.onSelectProject(newProject(name = "newProject"))

        verify(view).updateProjectName(projectName = "newProject")
    }

    @Test
    fun shouldShowLoaderOnSaveReport() {
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0", "description")

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnSaveReportFinish() {
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0", "description")

        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSavingHasNotFinished() {
        stubEditReportServiceToReturnNever()
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0", "description")

        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSavingHasNotFinished() {
        stubEditReportServiceToReturnNever()
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0", "description")
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowErrorWhenSavingReportFails() {
        stubEditReportServiceToReturnError()
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0", "description")

        verify(view).showError(any())
    }

    @Test
    fun shouldCloseViewWhenSavingHasNotFailed() {
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0", "description")

        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnRemoveReport() {
        controller.onCreate(newRegularHourlyReport())

        controller.onRemoveReport()

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnRemoveReport() {
        controller.onCreate(newRegularHourlyReport())

        controller.onRemoveReport()

        verify(view).hideLoader()
    }

    @Test
    fun shouldCloseViewWhenRemoveReportHasNotFailed() {
        controller.onCreate(newRegularHourlyReport())

        controller.onRemoveReport()

        verify(view).close()
    }

    @Test
    fun shouldShowErrorWhenRemoveReportFails() {
        stubRemoveReportApiToReturnError()

        controller.onCreate(newRegularHourlyReport())
        controller.onRemoveReport()

        verify(view).showError(any())
    }

    @Test
    fun shouldHideLoaderOnDestroyIfRemoveReportHasNotFinished() {
        stubRemoveReportApiToReturn(Completable.never())

        controller.onCreate(newRegularHourlyReport())
        controller.onRemoveReport()
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowDateOnCreate() {
        controller.onCreate(newRegularHourlyReport(year = 2011, month = 10, day = 1))

        verify(view).showDate("2011-10-01")
    }

    @Test
    fun shouldShowSelectedDate() {
        controller.onCreate(newRegularHourlyReport())

        controller.onDateSelect("2016-05-04")

        verify(view).showDate("2016-05-04")
    }

    @Test
    fun shouldChangeDateAfterOnCreate() {
        controller.onCreate(newRegularHourlyReport())

        controller.onDateSelect("2016-05-04")
        controller.onSaveReport("0.1", "Desription")

        verify(editReportService).edit(argThat { day == 4 && month == 5 && year == 2016 })
    }

    private fun stubEditReportServiceToReturnNever() {
        stubEditReportServiceToReturn(Completable.never())
    }

    private fun stubEditReportServiceToReturnError() {
        stubEditReportServiceToReturn(Completable.error(RuntimeException()))
    }

    private fun stubEditReportServiceToReturnSuccess() {
        stubEditReportServiceToReturn(Completable.complete())
    }

    private fun stubEditReportServiceToReturn(completable: Completable) {
        whenever(editReportService.edit(any<RegularHourlyReport>())).thenReturn(completable)
    }

    private fun stubRemoveReportApiToReturnError() {
        stubRemoveReportApiToReturn(Completable.error(RuntimeException()))
    }

    private fun stubRemoveReportApiToReturnSuccess() {
        stubRemoveReportApiToReturn(Completable.complete())
    }

    private fun stubRemoveReportApiToReturn(completable: Completable) {
        whenever(removeReportApi.removeReport(any())).thenReturn(completable)
    }
}