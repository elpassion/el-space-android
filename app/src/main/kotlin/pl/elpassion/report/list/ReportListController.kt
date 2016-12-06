package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers

import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.*
import pl.elpassion.report.Report
import rx.Observable
import rx.Subscription
import java.util.*

class ReportListController(val service: ReportList.Service, val view: ReportList.View) : OnDayClickListener, OnReportClickListener {

    private var subscription: Subscription? = null
    private val initialDateCalendar: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }
    private val dateChangeObserver by lazy { DateChangeObserver(initialDateCalendar) }

    fun onCreate() {
        fetchReports()
    }

    fun refreshReportList() {
        fetchReports()
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    private fun observeDateChange() = dateChangeObserver.observe()
            .doOnNext { view.showMonthName(it.month.monthName) }

    private fun fetchReports() {
        subscription = Observable.combineLatest(observeDateChange(),
                fetchReportsFromApi(), { t1, t2 -> Pair(t1, t2) })
                .map { createDaysWithReports(it.first, it.second) }
                .subscribe({ days ->
                    view.showDays(days, this, this)
                }, {
                    view.showError(it)
                })
    }

    private fun fetchReportsFromApi() =
            service.getReports()
                    .applySchedulers()
                    .doOnSubscribe { view.showLoader() }
                    .doOnUnsubscribe { view.hideLoader() }


    fun onNextMonth() {
        dateChangeObserver.setNextMonth()
    }

    fun onPreviousMonth() {
        dateChangeObserver.setPreviousMonth()
    }

    private fun createDaysWithReports(yearMonth: YearMonth, reportList: List<Report>): List<Day> {
        val days = (1..yearMonth.month.daysInMonth).map { dayNumber ->
            val calendarForDay = getCalendarForDay(yearMonth, dayNumber)
            Day(reports = reportList.filter(isFromSelectedDay(yearMonth, dayNumber)),
                    hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                    isWeekendDay = calendarForDay.isWeekendDay(),
                    name = "$dayNumber ${calendarForDay.dayName()}",
                    date = getPerformedAtString(yearMonth.year, yearMonth.month.index + 1, dayNumber))
        }

        return days
    }

    private fun isFromSelectedDay(yearMonth: YearMonth, day: Int): (Report) -> Boolean = { report ->
        report.year == yearMonth.year && report.month == yearMonth.month.index + 1 && report.day == day
    }

    private fun getCalendarForDay(yearMonth: YearMonth, dayNumber: Int) = getTimeFrom(year = yearMonth.year, month = yearMonth.month.index, day = dayNumber)

    override fun onDayDate(date: String) {
        view.openAddReportScreen(date)
    }

    override fun onReport(report: Report) {
        view.openEditReportScreen(report)
    }
}

interface OnDayClickListener {
    fun onDayDate(date: String)
}

interface OnReportClickListener {
    fun onReport(report: Report)
}
