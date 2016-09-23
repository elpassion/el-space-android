package pl.elpassion.report.list

import pl.elpassion.common.*
import rx.Subscription
import java.util.*

class ReportListController(val service: ReportList.Service, val view: ReportList.View) : OnDayClickListener {

    private var subscription: Subscription? = null
    private val reportList: MutableList<Report> = ArrayList()
    private val date: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }

    fun onCreate() {
        subscription = service.getReports()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({ reports ->
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
        val days = ArrayList<Day>().apply {
            (1..daysForCurrentMonth()).forEach {
                add(Day(it, reportList.filter(isFromSelectedDay(it)), isNotAfterNow(it), isWeekendDay = isWeekendDay(it)))
            }
        }

        view.showDays(days, this)
        view.showMonthName(date.getFullMonthName())
    }

    private fun isWeekendDay(dayNumber: Int): Boolean {
        val iteratorDay = getTimeFrom(year = date.get(Calendar.YEAR), month = date.get(Calendar.MONTH), day = dayNumber)
        return iteratorDay.isWeekendDay()
    }

    private fun daysForCurrentMonth() = date.getActualMaximum(Calendar.DAY_OF_MONTH)

    private fun isFromSelectedDay(day: Int): (Report) -> Boolean = { report ->
        report.year == date.get(Calendar.YEAR) && report.month == date.get(Calendar.MONTH) + 1 && report.day == day
    }

    private val isNotAfterNow: (Int) -> Boolean = { dayNumber ->
        val currentDate = getCurrentTimeCalendar()
        val iteratorDay = getTimeFrom(year = date.get(Calendar.YEAR), month = date.get(Calendar.MONTH), day = dayNumber)
        iteratorDay.isNotAfter(currentDate)
    }

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
