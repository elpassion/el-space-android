package pl.elpassion.elspace.hub.report.list

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
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
                           private val schedulers: SchedulersSupplier) {

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

    fun onDayClick(date: String) {
        view.openAddReportScreen(date)
    }

    fun onReportClick(report: Report) = view.openEditReportScreen(report)

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
        Observables.combineLatest(fetchDays(), actions.reportsFilter(),
                { list: List<AdapterItem>, shouldFilter: Boolean ->
                    when (shouldFilter) {
                        true -> dayFilter.fetchFilteredDays(list)
                        else -> list
                    }
                })
                .subscribe({ days ->
                    view.showDays(days)
                }, {
                })
                .addTo(subscriptions)
    }

    private fun refreshingDataObservable() = Observable.merge(Observable.just(Unit),
            actions.resultRefresh(), actions.refreshingEvents(), actions.snackBarRetry())

    private fun fetchDays() = refreshingDataObservable()
            .switchMap {
                dateChangeObserver
                        .observe()
                        .observeOn(schedulers.backgroundScheduler)
                        .flatMap(reportDayService::createDays)
                        .subscribeOn(schedulers.backgroundScheduler)
                        .observeOn(schedulers.uiScheduler)
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

    private fun isCurrentYearAndMonth() = dateChangeObserver.lastDate.let {
        it.year == calendar.year && it.month.index == calendar.month
    }
}

typealias OnDayClick = (String) -> Unit

typealias OnReportClick = (Report) -> Unit