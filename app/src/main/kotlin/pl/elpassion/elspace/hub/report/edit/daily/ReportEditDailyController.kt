package pl.elpassion.elspace.hub.report.edit.daily

import pl.elpassion.elspace.api.applySchedulers
import pl.elpassion.elspace.common.extensions.dayOfMonth
import pl.elpassion.elspace.common.extensions.month
import pl.elpassion.elspace.common.extensions.toCalendarDate
import pl.elpassion.elspace.common.extensions.year
import pl.elpassion.elspace.hub.report.DailyReport
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import kotlin.properties.Delegates

class ReportEditDailyController(private val view: ReportEdit.Daily.View,
                                private val editReportApi: ReportEdit.Daily.Service) {

    private var report: DailyReport by Delegates.notNull()

    fun onCreate(report: DailyReport) {
        view.showDate(report.date)
        this.report = report
    }

    fun onDateSelect(date: String) {
        val calendar = date.toCalendarDate()
        report = report.copy(day = calendar.dayOfMonth, month = calendar.month + 1, year = calendar.year)
        view.showDate(report.date)
    }

    fun onSaveReport() {
        editReportApi.edit(report)
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe(
                        { view.close() },
                        { view.showError(it) }
                )
    }

    fun onDestroy() {
        view.hideLoader()
    }

}