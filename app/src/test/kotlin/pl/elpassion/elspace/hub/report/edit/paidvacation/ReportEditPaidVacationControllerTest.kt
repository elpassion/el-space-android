package pl.elpassion.elspace.hub.report.edit.paidvacation

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.commons.RxSchedulersRule
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import rx.Completable

class ReportEditPaidVacationControllerTest {

    private val view = mock<ReportEdit.PaidVacation.View>()
    private val editReportService = mock<ReportEdit.PaidVacation.Service>()
    private val removeReportApi = mock<ReportEdit.RemoveApi>()
    private val controller = ReportEditPaidVacationController(view, editReportService, removeReportApi)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubEditReportServiceToReturnSuccess()
        stubRemoveReportApiToReturnSuccess()
    }

    @Test
    fun shouldShowCorrectReportOnCreate() {
        val report = newPaidVacationHourlyReport()

        controller.onCreate(report)

        verify(view).showReportHours(report.reportedHours)
    }


    @Test
    fun shouldCallServiceWithCorrectDataOnSaveReport() {
        val report = newPaidVacationHourlyReport(year = 2017, month = 7, day = 2, id = 2, reportedHours = 4.0)
        controller.onCreate(report)

        controller.onSaveReport(hours = "8.0")

        verify(editReportService).edit(report.copy(year = 2017, month = 7, day = 2, id = 2, reportedHours = 8.0))
    }

    @Test
    fun shouldReallyCallServiceWithCorrectDataOnSaveReport() {
        val report = newPaidVacationHourlyReport(year = 2016, month = 1, day = 3, id = 5)
        controller.onCreate(report)

        controller.onSaveReport(hours = "7.5")

        verify(editReportService).edit(report.copy(year = 2016, month = 1, day = 3, id = 5, reportedHours = 7.5))
    }

    @Test
    fun shouldShowLoaderOnSaveReport() {
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnSaveReportFinish() {
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")

        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSavingHasNotFinished() {
        stubEditReportServiceToReturnNever()
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")

        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSavingHasNotFinished() {
        stubEditReportServiceToReturnNever()
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowErrorWhenSavingReportFails() {
        stubEditReportServiceToReturnError()
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")

        verify(view).showError(any())
    }

    @Test
    fun shouldCloseViewWhenSavingHasNotFailed() {
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")

        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnRemoveReport() {
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onRemoveReport()

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnRemoveReport() {
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onRemoveReport()

        verify(view).hideLoader()
    }

    @Test
    fun shouldCloseViewWhenRemoveReportHasNotFailed() {
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onRemoveReport()

        verify(view).close()
    }

    @Test
    fun shouldShowErrorWhenRemoveReportFails() {
        stubRemoveReportApiToReturnError()

        controller.onCreate(newPaidVacationHourlyReport())
        controller.onRemoveReport()

        verify(view).showError(any())
    }

    @Test
    fun shouldHideLoaderOnDestroyIfRemoveReportHasNotFinished() {
        stubRemoveReportApiToReturn(Completable.never())

        controller.onCreate(newPaidVacationHourlyReport())
        controller.onRemoveReport()
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowDateOnCreate() {
        controller.onCreate(newPaidVacationHourlyReport(year = 2011, month = 10, day = 1))

        verify(view).showDate("2011-10-01")
    }

    @Test
    fun shouldShowSelectedDate() {
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onDateSelect("2016-05-04")

        verify(view).showDate("2016-05-04")
    }

    @Test
    fun shouldChangeDateAfterOnCreate() {
        val report = newPaidVacationHourlyReport()
        controller.onCreate(report)

        controller.onDateSelect("2016-05-04")
        controller.onSaveReport("0.1")

        verify(editReportService).edit(report.copy(year = 2016, month = 5, day = 4, reportedHours = 0.1))
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
        whenever(editReportService.edit(any())).thenReturn(completable)
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