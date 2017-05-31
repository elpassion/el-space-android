package pl.elpassion.elspace.hub.report.list

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.list.service.DayFilter
import pl.elpassion.elspace.hub.report.list.service.ReportDayService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers.trampoline
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import java.util.*

class ReportListControllerTest {

    private val service = mock<ReportDayService>()
    private val actions = mock<ReportList.Actions> {
        on { snackBarRetry() } doReturn Observable.empty()
        on { resultRefresh() } doReturn Observable.empty()
    }
    private val filter = mock<DayFilter>()
    private val view = mock<ReportList.View>()
    private val controller = ReportListController(service, filter, actions, view, SchedulersSupplier(backgroundScheduler = trampoline(), uiScheduler = trampoline()))
    private val daysSubject = PublishSubject.create<List<Day>>()
    private val filterSubject = PublishSubject.create<Boolean>()

    @Before
    fun setUp() {
        whenever(service.createDays(any())).thenReturn(daysSubject)
        whenever(actions.reportsFilter()).thenReturn(filterSubject)
        stubCurrentTime()
        stubViewActions()
    }

    @Test
    fun shouldShowCorrectMonthNameOnCreate() {
        stubDateChangeToReturn(getTimeFrom(2016, 0, 20))
        controller.onCreate()
        verify(view).showMonthName("January")
    }

    @Test
    fun shouldCallActionFilterPrevOnCreate() {
        controller.onCreate()
        verify(actions).reportsFilter()
    }

    @Test
    fun shouldCallActionMonthChangeNextOnCreate() {
        controller.onCreate()
        verify(actions).monthChangeToNext()
    }


    @Test
    fun shouldCallActionMonthChangePrevOnCreate() {
        controller.onCreate()
        verify(actions).monthChangeToPrev()
    }

    @Test
    fun shouldCallActionReportAddOnCreate() {
        controller.onCreate()
        verify(actions).reportAdd()
    }

    @Test
    fun shouldCallActionScrollToCurrentOnCreate() {
        controller.onCreate()
        verify(actions).scrollToCurrent()
    }


    @Test
    fun shouldReallyShowCorrectMonthNameOnCreate() {
        stubDateChangeToReturn(getTimeFrom(2016, 10, 20))
        controller.onCreate()
        verify(view).showMonthName("November")
    }

    @Test
    fun shouldShowErrorWhenApiCallFails() {
        controller.onCreate()
        daysSubject.onError(RuntimeException())
        verify(view, times(1)).showError(any())
    }

    @Test
    fun shouldShowLoaderWhenApiCallBeginsOnStart() {
        whenever(view.isDuringPullToRefresh()).thenReturn(false)
        controller.onCreate()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallSuccessful() {
        controller.onCreate()
        daysSubject.onNext(emptyList())
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallError() {
        controller.onCreate()
        daysSubject.onError(RuntimeException())
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotShowLoaderWhenDuringPullRefresh() {
        whenever(view.isDuringPullToRefresh()).thenReturn(true)
        controller.onCreate()
        verify(view, never()).showLoader()
    }

    @Test
    fun shouldOpenAddReportScreenOnDay() {
        controller.onDayClick("1999-01-02")
        verify(view, times(1)).openAddReportScreen("1999-01-02")
    }

    @Test
    fun shouldOpenEditReportScreenOnReport() {
        val report = newRegularHourlyReport()
        controller.onReportClick(report)
        verify(view, times(1)).openEditReportScreen(report)
    }

    @Test
    fun shouldOpenAddReportScreen() {
        stubCurrentTime(year = 2017, month = 1, day = 30)
        whenever(actions.reportAdd()).thenReturn(Observable.just(Unit))
        ReportListController(service, filter, actions, view, SchedulersSupplier(backgroundScheduler = trampoline(), uiScheduler = trampoline())).onCreate()
        verify(view).openAddReportScreen("2017-01-30")
    }

    @Test
    fun shouldCallServiceTwiceWhenPullToRefreshCalledOnCreate() {
        whenever(actions.refreshingEvents()).thenReturn(Observable.just(Unit))
        controller.onCreate()
        daysSubject.onNext(emptyList())
        verify(service, times(2)).createDays(any())
    }

    @Test
    fun shouldScrollToCorrectPositionOnTodayWhenNoReports() {
        stubCurrentTime(2017, 1, 20)
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))
        controller.onCreate()
        daysSubject.onNext(emptyList())
        controller.updateLastPassedDayPosition(20)

        verify(view).scrollToPosition(20)
    }

    @Ignore("Investigate why it fails with 31 day of month")
    @Test
    fun shouldReallyScrollToCorrectPositionOnTodayWhenNoReports() {
        stubCurrentTime(2017, 1, 31)
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))
        controller.onCreate()
        daysSubject.onNext(emptyList())
        controller.updateLastPassedDayPosition(31)
        verify(view).scrollToPosition(31)
    }

    @Test
    fun shouldScrollToCorrectPositionAndChangeMonthOnTodayWhenMonthIsNotCurrent() {
        stubCurrentTime(2017, 1, 10)
        whenever(actions.monthChangeToPrev()).thenReturn(Observable.just(Unit))
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))
        controller.onCreate()
        daysSubject.onNext(emptyList())
        controller.updateLastPassedDayPosition(10)
        verify(view).scrollToPosition(10)
        verify(view, times(3)).showMonthName(any())
    }

    @Test
    fun shouldScrollToCorrectPositionAndReallyChangeMonthOnTodayWhenMonthIsNotCurrent() {
        stubCurrentTime(2017, 2, 10)
        whenever(actions.monthChangeToPrev()).thenReturn(Observable.just(Unit, Unit))
        whenever(actions.scrollToCurrent()).thenReturn(Observable.just(Unit))
        controller.onCreate()
        daysSubject.onNext(emptyList())
        controller.updateLastPassedDayPosition(10)
        verify(view).scrollToPosition(10)
        verify(view, times(4)).showMonthName(any())
    }

    @Test
    fun shouldListenForFilterActions() {
        controller.onCreate()
        daysSubject.onNext(emptyList())
        verify(actions).reportsFilter()
    }

    @Test
    fun shouldUseFilterActionBeforeShowingReportsList() {
        whenever(actions.reportsFilter()).thenReturn(Observable.never())
        controller.onCreate()
        daysSubject.onNext(emptyList())
        verify(view, never()).showDays(any(), any(), any())
    }

    @Test
    fun shouldDoNotFilterDaysWhenFilterIsOff() {
        controller.onCreate()
        daysSubject.onNext(emptyList())
        verify(filter, never()).fetchFilteredDays(any())
    }


    @Test
    fun shouldCallShowDaysTwiceWhenFilterIsChanged() {
        controller.onCreate()
        filterSubject.onNext(false)
        daysSubject.onNext(emptyList())
        filterSubject.onNext(true)
        verify(view, times(2)).showDays(any(), any(), any())
    }

    @Test
    fun shouldCallServiceTwiceWhenRetryFromSnackBar() {
        whenever(actions.snackBarRetry()).thenReturn(Observable.just(Unit))
        controller.onCreate()
        verify(service, times(2)).createDays(any())
    }

    @Test
    fun shouldFilterDaysWhenFilterIsOn() {
        controller.onCreate()
        daysSubject.onNext(emptyList())
        filterSubject.onNext(true)
        verify(filter).fetchFilteredDays(any())
    }

    @Test
    fun shouldListenForAllRefreshActionsOnCreate() {
        controller.onCreate()
        verify(actions).refreshingEvents()
        verify(actions).snackBarRetry()
        verify(actions).resultRefresh()
    }

    @Test
    fun shouldSubscribeOnGivenScheduler() {
        val subscribeOnScheduler = TestScheduler()
        val controller = ReportListController(service, filter, actions, view, SchedulersSupplier(subscribeOnScheduler, trampoline()))
        controller.onCreate()
        daysSubject.onError(RuntimeException())
        verify(view, never()).showError(any())
        subscribeOnScheduler.triggerActions()
        verify(view).showError(any())
    }

    @Test
    fun shouldObserveOnGivenScheduler() {
        val observeOnScheduler = TestScheduler()
        val controller = ReportListController(service, filter, actions, view, SchedulersSupplier(trampoline(), observeOnScheduler))
        controller.onCreate()
        daysSubject.onError(RuntimeException())
        verify(view, never()).showError(any())
        observeOnScheduler.triggerActions()
        verify(view).showError(any())
    }

    private fun stubViewActions() {
        whenever(actions.refreshingEvents()).thenReturn(Observable.never())
        whenever(actions.reportAdd()).thenReturn(Observable.never())
        whenever(actions.monthChangeToNext()).thenReturn(Observable.never())
        whenever(actions.monthChangeToPrev()).thenReturn(Observable.never())
        whenever(actions.scrollToCurrent()).thenReturn(Observable.never())
    }

    private fun stubDateChangeToReturn(cal: Calendar) {
        CurrentTimeProvider.override = { cal.timeInMillis }
    }

}
