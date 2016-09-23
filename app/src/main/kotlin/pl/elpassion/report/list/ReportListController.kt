package pl.elpassion.report.list

import pl.elpassion.common.*
import rx.Subscription
import java.util.*

class ReportListController(val api: ReportList.Api, val view: ReportList.View) {

    private var subscription: Subscription? = null
    private val reportList: MutableList<Report> = ArrayList()
    private val date: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }

    fun onCreate() {
        subscription = api.getReports()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({ reports ->
                    reportList.addAll(reports)
                    showDays()
                }, {
                    view.showError()
                })

        view.showMonth(date.getFullMonthName())
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    fun onNextMonth() {
        date.changeToNextMonth()
        showDays()
        view.showMonth(date.getFullMonthName())
    }

    fun onPreviousMonth() {
        date.changeToPreviousMonth()
        showDays()
    }

    private fun showDays() {
        val days = ArrayList<Day>().apply {
            (1..daysForCurrentMonth()).forEach {
                add(Day(it, reportList.filter(isFromSelectedDay(it)), isNotAfterNow(it)))
            }
        }

        view.showDays(days)
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

    fun onDay(dayNumber: Int) {
        view.openAddReportScreen(String.format("%d-%02d-%02d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, dayNumber))
    }
}