package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.report.Report
import pl.elpassion.report.list.service.DateChangeObserverImpl
import pl.elpassion.report.list.service.ReportDayService
import pl.elpassion.report.list.service.ReportDayServiceImpl
import rx.Subscription
import java.util.*

class ReportListController(val service: ReportList.Service, val view: ReportList.View) : OnDayClickListener, OnReportClickListener {

    private var subscription: Subscription? = null
    private val initialDateCalendar: Calendar by lazy { Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) } }
    private val dateChangeObserver by lazy { DateChangeObserverImpl(initialDateCalendar) }
    private lateinit var reportDayServiceImpl: ReportDayService
    fun onCreate() {
        reportDayServiceImpl = ReportDayServiceImpl(dateChangeObserver, service)
        fetchReports()
        subscribeDateChange()
    }

    fun refreshReportList() {
        fetchReports()
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    private fun fetchReports() {
        subscription = reportDayServiceImpl.createDays()
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

    private fun subscribeDateChange() = reportDayServiceImpl.observeDateChanges()
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
