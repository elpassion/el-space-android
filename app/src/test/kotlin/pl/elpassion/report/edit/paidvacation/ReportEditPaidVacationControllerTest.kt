package pl.elpassion.report.edit.paidvacation

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.project.dto.newPaidVacationHourlyReport
import pl.elpassion.project.dto.performedDate
import pl.elpassion.report.edit.ReportEdit
import rx.Completable

class ReportEditPaidVacationControllerTest {

    private val view = mock<ReportEdit.PaidVacation.View>()
    private val editReportApi = mock<ReportEdit.PaidVacation.Service>()
    private val removeReportApi = mock<ReportEdit.RemoveApi>()
    private val controller = ReportEditPaidVacationController(view, editReportApi, removeReportApi)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubEditReportApiToReturnSuccess()
        stubRemoveReportApiToReturnSuccess()
    }

    @Test
    fun shouldShowCorrectReportOnCreate() {
        val report = newPaidVacationHourlyReport()

        controller.onCreate(report)

        verify(view).showReportHours(report.reportedHours)
    }


    @Test
    fun shouldCallApiWithCorrectDataOnSaveReport() {
        val report = newPaidVacationHourlyReport(year = 2017, month = 7, day = 2, id = 2, reportedHours = 4.0)
        val date = getPerformedAtString(report.year, report.month, report.day)
        controller.onCreate(report)

        controller.onSaveReport(hours = "8.0")

        verify(editReportApi).edit(id = report.id, date = date, reportedHours = 8.0)
    }

    @Test
    fun shouldReallyCallApiWithCorrectDataOnSaveReport() {
        val report = newPaidVacationHourlyReport(year = 2016, month = 1, day = 3, id = 5)
        controller.onCreate(report)

        controller.onSaveReport(hours = "7.5")

        verify(editReportApi).edit(id = report.id, date = report.performedDate(), reportedHours = 7.5)
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
        stubEditReportApiToReturnNever()
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")

        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSavingHasNotFinished() {
        stubEditReportApiToReturnNever()
        controller.onCreate(newPaidVacationHourlyReport())

        controller.onSaveReport("1.0")
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowErrorWhenSavingReportFails() {
        stubEditReportApiToReturnError()
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

        verify(editReportApi).edit(id = report.id, date = "2016-05-04", reportedHours = 0.1)
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
        whenever(editReportApi.edit(any(), any(), any())).thenReturn(completable)
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