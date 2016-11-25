package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.*
import rx.Subscription
import java.util.*

class ReportListController(val service: ReportList.Service, val view: ReportList.View) : OnDayClickListener {

    private var subscription: Subscription? = null
    private val reportList: MutableList<Report> = ArrayList()
    private val date: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }

    fun onCreate() {
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
        val days = (1..daysForCurrentMonth()).map { dayNumber ->
            val calendarForDay = getCalendarForDay(dayNumber)
            Day(dayNumber = dayNumber,
                    reports = reportList.filter(isFromSelectedDay(dayNumber)),
                    hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                    isWeekendDay = calendarForDay.isWeekendDay(),
                    name = "$dayNumber ${calendarForDay.dayName()}")
        }

        view.showDays(days, this)
        view.showMonthName(date.getFullMonthName())
    }

    private fun daysForCurrentMonth() = date.getActualMaximum(Calendar.DAY_OF_MONTH)

    private fun isFromSelectedDay(day: Int): (Report) -> Boolean = { report ->
        report.year == date.get(Calendar.YEAR) && report.month == date.get(Calendar.MONTH) + 1 && report.day == day
    }

    private fun getCalendarForDay(dayNumber: Int) = getTimeFrom(year = date.get(Calendar.YEAR), month = date.get(Calendar.MONTH), day = dayNumber)

    override fun onDay(dayNumber: Int) {
        view.openAddReportScreen(String.format("%d-%02d-%02d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, dayNumber))
    }

    fun onReport(report: Report) {
        view.openEditReportScreen(report)
    }
}

interface OnDayClickListener {
    fun onDay(dayNumber: Int)
}
