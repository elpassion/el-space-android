package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.report.Report
import pl.elpassion.report.list.service.DateChangeObserverImpl
import pl.elpassion.report.list.service.ReportDayService
import rx.Subscription
import java.util.*

class ReportListController(val reportDayService: ReportDayService,
                           val view: ReportList.View) : OnDayClickListener, OnReportClickListener {
    private var subscription: Subscription? = null
    private var dateChangeSubscription: Subscription? = null
    private val dateChangeObserver by lazy { DateChangeObserverImpl(Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }) }

    fun onCreate() {
        fetchReports()
        subscribeDateChange()
    }

    fun refreshReportList() {
        fetchReports()
    }

    fun onDestroy() {
        subscription?.unsubscribe()
        dateChangeSubscription?.unsubscribe()
    }

    private fun fetchReports() {
        subscription = reportDayService.createDays(dateChangeObserver.observe())
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

    private fun subscribeDateChange() {
        dateChangeSubscription = dateChangeObserver.observe()
                .subscribe { view.showMonthName(it.month.monthName) }
    }

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
