package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.report.HoursReport
import pl.elpassion.report.list.service.DateChangeObserver
import pl.elpassion.report.list.service.ReportDayService
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.util.*

class ReportListController(private val reportDayService: ReportDayService,
                           private val view: ReportList.View) : OnDayClickListener, OnReportClickListener {

    private val subscriptions = CompositeSubscription()
    private val dateChangeObserver by lazy { DateChangeObserver(Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }) }

    fun onCreate() {
        fetchReports()
        subscribeDateChange()
    }

    fun refreshReportList() {
        fetchReports()
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    private fun fetchReports() {
        reportDayService.createDays(dateChangeObserver.observe())
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({ days ->
                    view.hideLoader()
                    view.showDays(days, this, this)
                }, {
                    view.showError(it)
                })
                .save()
    }

    private fun subscribeDateChange() {
        dateChangeObserver.observe()
                .subscribe { view.showMonthName(it.month.monthName) }
                .save()
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

    override fun onReport(report: HoursReport) {
        view.openEditReportScreen(report)
    }

    private fun Subscription.save() {
        subscriptions.add(this)
    }

    fun onAddTodayReport() {
        view.openAddReportScreen()
    }
}

interface OnDayClickListener {
    fun onDayDate(date: String)
}

interface OnReportClickListener {
    fun onReport(report: HoursReport)
}
