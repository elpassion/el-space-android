package pl.elpassion.report.list

import pl.elpassion.common.CurrentTimeProvider
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
                    val days = ArrayList<Day>()
                    (1..daysForCurrentMonth()).forEach { days.add(Day(it, reportList.filter(getReportsForDay(it)))) }
                    view.showDays(days)
                }, {
                    view.showError()
                })

    }

    private fun getReportsForDay(day: Int): (Report) -> Boolean {
        return { report ->
            report.year == date.get(Calendar.YEAR) && report.month == date.get(Calendar.MONTH) + 1 && report.day == day
        }
    }

    private fun daysForCurrentMonth() = date.getActualMaximum(Calendar.DAY_OF_MONTH)

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    fun onNextMonth() {
        date.add(Calendar.MONTH, 1)
        val days = ArrayList<Day>()
        (1..daysForCurrentMonth()).forEach { days.add(Day(it, reportList.filter(getReportsForDay(it)))) }
        view.showDays(days)
    }
}