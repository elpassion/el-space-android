package pl.elpassion.report.edit.daily

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.extensions.dayOfMonth
import pl.elpassion.common.extensions.month
import pl.elpassion.common.extensions.toCalendarDate
import pl.elpassion.common.extensions.year
import pl.elpassion.report.DailyReport
import pl.elpassion.report.edit.ReportEdit
import kotlin.properties.Delegates

class ReportEditDailyController(val view: ReportEdit.Daily.View,
                                val editReportApi: ReportEdit.Daily.Service) {

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