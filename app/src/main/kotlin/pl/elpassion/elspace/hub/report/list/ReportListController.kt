package pl.elpassion.elspace.hub.report.list

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.*
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.service.DateChangeObserver
import pl.elpassion.elspace.hub.report.list.service.DayFilter
import pl.elpassion.elspace.hub.report.list.service.ReportDayService

class ReportListController(private val reportDayService: ReportDayService,
                           private val dayFilter: DayFilter,
                           private val actions: ReportList.Actions,
                           private val view: ReportList.View,
                           private val schedulers: SchedulersSupplier) : OnDayClickListener, OnReportClickListener {

    private val subscriptions = CompositeDisposable()
    private val dateChangeObserver by lazy { DateChangeObserver(getCurrentTimeCalendar()) }
    private val todayPositionObserver = TodayPositionObserver()
    private val calendar = getCurrentTimeCalendar()

    fun onCreate() {
        fetchReports()
        subscribeDateChange()
        Observable.merge(
                actions.reportAdd().doOnNext { view.openAddReportScreen(calendar.getDateString()) },
                actions.monthChangeToNext().doOnNext { dateChangeObserver.setNextMonth() },
                actions.monthChangeToPrev().doOnNext { dateChangeObserver.setPreviousMonth() },
                actions.scrollToCurrent().doOnNext { onToday() })
                .subscribe()
                .addTo(subscriptions)
    }

    fun updateLastPassedDayPosition(position: Int) {
        if (isCurrentYearAndMonth()) {
            todayPositionObserver.updatePosition(position)
        }
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    private fun onToday() {
        if (isCurrentYearAndMonth()) {
            scrollToTodayPosition(todayPositionObserver.lastPosition)
        } else {
            dateChangeObserver.setYearMonth(calendar.year, calendar.month)
            todayPositionObserver.observe().filter { it != -1 }
                    .subscribe { scrollToTodayPosition(it) }.addTo(subscriptions)
        }
    }

    private fun scrollToTodayPosition(todayPosition: Int) {
        if (todayPosition != -1) {
            view.scrollToPosition(todayPosition)
        }
    }

    private fun fetchReports() {
        combineLatest(fetchDays(), actions.reportsFilter(),
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
                reportDayService.createDays(dateChangeObserver.observe().observeOn(Schedulers.io()))
                        .subscribeOn(schedulers.subscribeOn)
                        .observeOn(schedulers.observeOn)
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
        view.openEditReportScreen(report)
    }

    private fun isCurrentYearAndMonth() = dateChangeObserver.lastDate.let {
        it.year == calendar.year && it.month.index == calendar.month
    }
}

interface OnDayClickListener {
    fun onDayDate(date: String)
}

interface OnReportClickListener {
    fun onReport(report: Report)
}
