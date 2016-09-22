package pl.elpassion.report.list

import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.changeToNextMonth
import pl.elpassion.common.changeToPreviousMonth
import pl.elpassion.common.isNotAfter
import rx.Subscription
import java.util.*

class ReportListController(val api: ReportList.Api, val view: ReportList.View) {

    var subscription: Subscription? = null
    val reportList: MutableList<Report> = ArrayList()
    val date: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }

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

    }

    private fun getReportsForDay(day: Int): (Report) -> Boolean = { report ->
        report.year == date.get(Calendar.YEAR) && report.month == date.get(Calendar.MONTH) + 1 && report.day == day
    }

    private fun daysForCurrentMonth() = date.getActualMaximum(Calendar.DAY_OF_MONTH)

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    fun onNextMonth() {
        date.changeToNextMonth()
        showDays()
    }

    fun onPreviousMonth() {
        date.changeToPreviousMonth()
        showDays()
    }

    private fun showDays() {
        val days = ArrayList<Day>().apply {
            (1..daysForCurrentMonth()).forEach {
                add(Day(it, reportList.filter(getReportsForDay(it)), isNotAfterNow(it)))
            }
        }

        view.showDays(days)
    }

    val isNotAfterNow: (Int) -> Boolean = { dayNumber ->
        val currentDate = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
        val iteratorDay = Calendar.getInstance().apply { set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), dayNumber) }
        iteratorDay.isNotAfter(currentDate)
    }
}