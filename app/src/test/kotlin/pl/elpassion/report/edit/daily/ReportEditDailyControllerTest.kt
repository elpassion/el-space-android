package pl.elpassion.report.edit.daily

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.report.DailyReportType
import pl.elpassion.report.edit.ReportEdit
import rx.Completable

class ReportEditDailyControllerTest {

    private val editReportApi = mock<ReportEdit.Daily.Service>()
    private val view = mock<ReportEdit.Daily.View>()
    private val controller = ReportEditDailyController(view, editReportApi)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubEditReportApiToReturnSuccess()
    }

    @Test
    fun shouldShowSelectedDateOnCreate() {
        val report = newDailyReport()
        controller.onCreate(report)

        verify(view).showDate(report.date)
    }

    @Test
    fun shouldCallApiWithCorrectDataOnSaveReport() {
        val report = newDailyReport(year = 2017, month = 7, day = 2, id = 2, reportType = DailyReportType.SICK_LEAVE)
        controller.onCreate(report)

        controller.onDateSelect("2017-01-03")
        controller.onSaveReport()

        verify(editReportApi).edit(report.copy(year = 2017, month = 1, day = 3, id = 2, reportType = DailyReportType.SICK_LEAVE))
    }

    @Test
    fun shouldShowChangedDateOnDateSelect() {
        val report = newDailyReport()
        controller.onCreate(report)

        controller.onDateSelect("2018-09-03")

        verify(view).showDate(report.copy(year = 2018, month = 9, day = 3).date)
    }

    @Test
    fun shouldShowLoaderOnSaveReport() {
        controller.onCreate(newDailyReport())

        controller.onSaveReport()

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnSaveReportFinish() {
        controller.onCreate(newDailyReport())

        controller.onSaveReport()

        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSavingHasNotFinished() {
        stubEditReportApiToReturnNever()
        controller.onCreate(newDailyReport())

        controller.onSaveReport()

        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSavingHasNotFinished() {
        stubEditReportApiToReturnNever()
        controller.onCreate(newDailyReport())

        controller.onSaveReport()
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldCloseViewWhenSavingHasNotFailed() {
        controller.onCreate(newDailyReport())

        controller.onSaveReport()

        verify(view).close()
    }

    @Test
    fun shouldShowErrorWhenSavingReportFails() {
        stubEditReportApiToReturnError()
        controller.onCreate(newDailyReport())

        controller.onSaveReport()

        verify(view).showError(any())
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
        whenever(editReportApi.edit(any())).thenReturn(completable)
    }
}