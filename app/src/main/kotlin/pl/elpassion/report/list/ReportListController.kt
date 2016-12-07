package pl.elpassion.report.list

import pl.elpassion.api.applySchedulers
import pl.elpassion.report.Report
import pl.elpassion.report.list.service.ReportDayService
import rx.Subscription

class ReportListController(val reportDayService: ReportDayService,
                           val view: ReportList.View) : OnDayClickListener, OnReportClickListener {

    private var subscription: Subscription? = null

    fun onCreate() {
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

    private fun subscribeDateChange() = reportDayService.observeDateChanges()
            .subscribe { view.showMonthName(it.month.monthName) }

    fun onNextMonth() {
        reportDayService.changeMonthToNext()
    }

    fun onPreviousMonth() {
        reportDayService.changeMonthToPrevious()
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
