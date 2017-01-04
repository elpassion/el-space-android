package pl.elpassion.report.edit.daily

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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

    private fun stubEditReportApiToReturnSuccess() {
        whenever(editReportApi.edit(any())).thenReturn(Completable.complete())
    }
}