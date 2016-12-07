package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.report.Report
import rx.Subscription
import java.util.*

class ReportListController(val service: ReportList.Service, val view: ReportList.View) : OnDayClickListener, OnReportClickListener {

    private var subscription: Subscription? = null
    private val initialDateCalendar: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }
    private val dateChangeObserver by lazy { DateChangeObserver(initialDateCalendar) }
    private lateinit var reportDayService: ReportDayService
    fun onCreate() {
        reportDayService = ReportDayService(dateChangeObserver, service)
        fetchReports()
        subsribeDateChange()
    }

    fun refreshReportList() {
        fetchReports()
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    private fun fetchReports() {
        subscription = reportDayService.createDays()
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({ days ->
                    view.hideLoader()
                    view.showDays(days, this, this)
                }, {
                    view.showError(it)
                })
    }

    private fun subsribeDateChange() = reportDayService.observeDateChanges()
            .subscribe { view.showMonthName(it.month.monthName) }

    fun onNextMonth() {
        dateChangeObserver.setNextMonth()
    }

    fun onPreviousMonth() {
        dateChangeObserver.setPreviousMonth()
    }

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
