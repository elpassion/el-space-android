package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.list.service.ReportDayService
import rx.Observable
import java.util.*

class ReportListControllerTest {

    val service = mock<ReportDayService>()
    val view = mock<ReportList.View>()
    val controller = ReportListController(service, view)

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubCurrentTime()
    }

    @Test
    fun shouldShowCorrectMonthNameOnCreate() {
        stubServiceToReturnNever()
        stubDateChangeToReturn(YearMonth(2016, Month(0, "January", 30)))

        controller.onCreate()

        verify(view).showMonthName("January")
    }

    @Test
    fun shouldReallyShowCorrectMonthNameOnCreate() {
        stubServiceToReturnEmptyList()
        stubDateChangeToReturn(YearMonth(2016, Month(0, "November", 30)))

        controller.onCreate()

        verify(view).showMonthName("November")
    }

    @Test
    fun shouldShowErrorWhenApiCallFails() {
        stubServiceToReturnError()
        stubDateChangeToReturn()

        controller.onCreate()

        verify(view, times(1)).showError(any())
    }

    @Test
    fun shouldShowLoaderWhenApiCallBegins() {
        stubServiceToReturnNever()
        stubDateChangeToReturn()

        controller.onCreate()

        verify(view, times(1)).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenCallIsNotFinishedOnDestroy() {
        stubServiceToReturnNever()
        stubDateChangeToReturn()

        controller.onCreate()
        controller.onDestroy()

        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallFinishes() {
        stubTodayDateAndEmptyList()

        controller.onCreate()

        verify(view, atLeast(1)).hideLoader()
    }

    @Test
    fun shouldCallChangeMonthToNext() {
        stubTodayDateAndEmptyList()

        controller.onCreate()
        controller.onNextMonth()

        verify(service).changeMonthToNext()
    }

    @Test
    fun shouldCallChangeMonthToPrevious() {
        stubTodayDateAndEmptyList()

        controller.onCreate()
        controller.onPreviousMonth()

        verify(service).changeMonthToPrevious()
    }

    private fun stubTodayDateAndEmptyList() {
        stubDateChangeToReturn()
        stubServiceToReturnEmptyList()
    }

    @Test
    fun shouldOpenAddReportScreenOnDay() {
        controller.onDayDate(date = "1999-01-02")

        verify(view, times(1)).openAddReportScreen("1999-01-02")
    }

    @Test
    fun shouldOpenEditReportScreenOnReport() {
        val report = newReport()
        controller.onReport(report)

        verify(view, times(1)).openEditReportScreen(report)
    }

    private fun stubServiceToReturnNever() {
        whenever(service.createDays()).thenReturn(Observable.never())
    }

    private fun stubServiceToReturnEmptyList() {
        whenever(service.createDays()).thenReturn(Observable.just(listOf()))
    }

    private fun stubServiceToReturnError() {
        whenever(service.createDays()).thenReturn(Observable.error(RuntimeException()))
    }

    private fun stubDateChangeToReturn(yearMonth: YearMonth = nowYearMonth()) {
        whenever(service.observeDateChanges()).thenReturn(Observable.just(yearMonth))
    }

    private fun nowYearMonth() = Calendar.getInstance().toYearMonth()

}
