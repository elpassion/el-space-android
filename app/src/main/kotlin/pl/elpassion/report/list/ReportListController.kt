package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.*
import pl.elpassion.report.DailyReport
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.Report
import pl.elpassion.report.list.service.DateChangeObserver
import pl.elpassion.report.list.service.DayFilter
import pl.elpassion.report.list.service.ReportDayService
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
            todayPositionObserver.observe().skip(1).first()
                    .subscribe { scrollToTodayPosition(it) }
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
