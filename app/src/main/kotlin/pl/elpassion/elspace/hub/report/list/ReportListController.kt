package pl.elpassion.elspace.hub.report.list

import pl.elpassion.elspace.api.applySchedulers
import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.extensions.*
import pl.elpassion.elspace.hub.report.DailyReport
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.service.DateChangeObserver
import pl.elpassion.elspace.hub.report.list.service.DayFilter
import pl.elpassion.elspace.hub.report.list.service.ReportDayService
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.util.*

class ReportListController(private val reportDayService: ReportDayService,
                           private val dayFilter: DayFilter,
                           private val actions: ReportList.Actions,
                           private val view: ReportList.View) : OnDayClickListener, OnReportClickListener {

    private val subscriptions = CompositeSubscription()
    private val dateChangeObserver by lazy { DateChangeObserver(Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }) }
    private val todayPositionObserver = TodayPositionObserver()
    private val calendar = getCurrentTimeCalendar()

    fun onCreate() {
        fetchReports()
        subscribeDateChange()
        Observable.merge(
                actions.reportAdd().doOnNext { view.openAddReportScreen() },
                actions.monthChangeToNext().doOnNext { dateChangeObserver.setNextMonth() },
                actions.monthChangeToPrev().doOnNext { dateChangeObserver.setPreviousMonth() },
                actions.scrollToCurrent().doOnNext { onToday() })
                .subscribe()
                .addTo(subscriptions)
    }

    fun updateTodayPosition(position: Int) {
        todayPositionObserver.updatePosition(position)
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    private fun onToday() {
        if (isNotCurrentYearOrMonth()) {
            dateChangeObserver.setYearMonth(calendar.year, calendar.month)
            todayPositionObserver.observe().filter { it != -1 }
                    .subscribe { scrollToTodayPosition(it) }.addTo(subscriptions)
        } else {
            scrollToTodayPosition(todayPositionObserver.lastPosition)
        }
    }

    private fun scrollToTodayPosition(todayPosition: Int) {
        if (todayPosition != -1) {
            view.scrollToPosition(todayPosition)
        }
    }

    private fun fetchReports() {
        Observable.combineLatest(fetchDays(), actions.reportsFilter(),
                { list: List<Day>, shouldFilter: Boolean ->
                    when (shouldFilter) {
                        true -> dayFilter.fetchFilteredDays(list)
                        else -> list
                    }
                })
                .subscribe({ days ->
                    view.showDays(days, this, this)
                }, {
                })
                .addTo(subscriptions)
    }

    private fun refreshingDataObservable() = Observable.merge(Observable.just(Unit),
            actions.resultRefresh(), actions.refreshingEvents(), actions.snackBarRetry())

    private fun fetchDays() = refreshingDataObservable()
            .switchMap {
                reportDayService.createDays(dateChangeObserver.observe())
                        .applySchedulers()
                        .doOnSubscribe {
                            if (!view.isDuringPullToRefresh()) {
                                view.showLoader()
                            }
                        }
                        .doOnNext { view.hideLoader() }
                        .catchOnError {
                            view.hideLoader()
                            view.showError(it)
                        }
            }

    private fun subscribeDateChange() {
        dateChangeObserver.observe()
                .subscribe { view.showMonthName(it.month.monthName) }
                .addTo(subscriptions)
    }

    override fun onDayDate(date: String) {
        view.openAddReportScreen(date)
    }

    override fun onReport(report: Report) {
        when (report) {
            is RegularHourlyReport -> view.openEditReportScreen(report)
            is PaidVacationHourlyReport -> view.openPaidVacationEditReportScreen(report)
            is DailyReport -> view.openDailyEditReportScreen(report)
        }
    }

    private fun isNotCurrentYearOrMonth() = dateChangeObserver.lastDate.let {
        it.year != calendar.year || it.month.index != calendar.month
    }
}

interface OnDayClickListener {
    fun onDayDate(date: String)
}

interface OnReportClickListener {
    fun onReport(report: Report)
}
