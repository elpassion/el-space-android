package pl.elpassion.report.edit

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newRegularHourlyReport
import pl.elpassion.report.RegularHourlyReport
import rx.Completable

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val editReportApi = mock<ReportEdit.Regular.Service>()
    private val removeReportApi = mock<ReportEdit.RemoveApi>()
    private val controller = ReportEditController(view, editReportApi, removeReportApi)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubEditReportApiToReturnSuccess()
        stubRemoveReportApiToReturnSuccess()
    }

    @Test
    fun shouldCallApiWithCorrectDataOnSaveReport() {
        val report = newRegularHourlyReport(year = 2017, month = 7, day = 2, id = 2, description = "DESCRIPTION", reportedHours = 4.0, project = newProject(id = 2))
        controller.onCreate(report)

        controller.onSaveReport(hours = "8.0")

        verify(editReportApi).edit(report.copy(reportedHours = 8.0))
    }

    @Test
    fun shouldReallyCallApiWithCorrectDataOnSaveReport() {
        val report = newRegularHourlyReport(year = 2016, month = 1, day = 3, id = 5, description = "DESCRIPTION", reportedHours = 4.0, project = newProject(id = 2))
        controller.onCreate(report)

        controller.onSaveReport(hours = "7.5")

        verify(editReportApi).edit(report.copy(reportedHours = 7.5))
    }

    @Test
    fun shouldShowLoaderOnSaveReport() {
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0")

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnSaveReportFinish() {
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0")

        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSavingHasNotFinished() {
        stubEditReportApiToReturnNever()
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0")

        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSavingHasNotFinished() {
        stubEditReportApiToReturnNever()
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0")
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowErrorWhenSavingReportFails() {
        stubEditReportApiToReturnError()
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0")

        verify(view).showError(any())
    }

    @Test
    fun shouldCloseViewWhenSavingHasNotFailed() {
        controller.onCreate(newRegularHourlyReport())

        controller.onSaveReport("1.0")

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
        controller.onSaveReport("0.1")

        verify(editReportApi).edit(argThat { day == 4 && month == 5 && year == 2016 })
    }

    private fun stubEditReportApiToReturnNever() {
        stubEditReportApiToReturn(Completable.never())
    }

    private fun stubEditReportApiToReturnError() {
        stubEditReportApiToReturn(Completable.error(RuntimeException()))
    }

    private fun stubEditReportApiToReturnSuccess() {
        stubEditReportApiToReturn(Completable.complete())
    }

    private fun stubEditReportApiToReturn(completable: Completable) {
        whenever(editReportApi.edit(any<RegularHourlyReport>())).thenReturn(completable)
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