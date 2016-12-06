package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers

import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.*
import pl.elpassion.report.Report
import rx.Subscription
import java.util.*

class ReportListController(val service: ReportList.Service, val view: ReportList.View) : OnDayClickListener, OnReportClickListener {

    private var subscription: Subscription? = null
    private val reportList: MutableList<Report> = ArrayList()
    private val date: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }

    fun onCreate() {
        fetchReports()
    }

    fun refreshReportList() {
        fetchReports()
    }

    private fun fetchReports() {
        subscription = service.getReports()
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({ reports ->
                    reportList.clear()
                    reportList.addAll(reports)
                    showDaysAndUpdateMonthName()
                }, {
                    view.showError(it)
                })
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    fun onNextMonth() {
        date.changeToNextMonth()
        showDaysAndUpdateMonthName()
    }

    fun onPreviousMonth() {
        date.changeToPreviousMonth()
        showDaysAndUpdateMonthName()
    }

    private fun showDaysAndUpdateMonthName() {
        val days = (1..date.daysForCurrentMonth()).map { dayNumber ->
            val calendarForDay = getCalendarForDay(dayNumber)
            Day(reports = reportList.filter(isFromSelectedDay(dayNumber)),
                    hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                    isWeekendDay = calendarForDay.isWeekendDay(),
                    name = "$dayNumber ${calendarForDay.dayName()}",
                    date = getPerformedAtString(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, dayNumber))
        }

        view.showDays(days, this, this)
        view.showMonthName(date.getFullMonthName())
    }

    private fun isFromSelectedDay(day: Int): (Report) -> Boolean = { report ->
        report.year == date.get(Calendar.YEAR) && report.month == date.get(Calendar.MONTH) + 1 && report.day == day
    }

    private fun getCalendarForDay(dayNumber: Int) = getTimeFrom(year = date.get(Calendar.YEAR), month = date.get(Calendar.MONTH), day = dayNumber)

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
