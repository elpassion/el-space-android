package pl.elpassion.report.list

import pl.elpassion.common.CurrentTimeProvider
import rx.Subscription
import java.util.*

class ReportListController(val api: ReportList.Api, val view: ReportList.View) {

    var subscription: Subscription? = null

    fun onCreate() {
        subscription = api.getReports()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({ reports ->
                    val days = ArrayList<Day>()
                    (1..daysForCurrentMonth()).forEach { days.add(Day(it, reports.filter(getReportsForDay(it)))) }
                    view.showDays(days)
                }, {
                    view.showError()
                })

    }

    private fun getReportsForDay(day: Int): (Report) -> Boolean {
        return { report ->
            val date = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
            report.year == date.get(Calendar.YEAR) && report.month == date.get(Calendar.MONTH) + 1 && report.day == day
        }
    }

    private fun daysForCurrentMonth() = Calendar.getInstance().run {
        time = Date(CurrentTimeProvider.get())
        getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }
}