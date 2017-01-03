package pl.elpassion.report.edit.daily

import pl.elpassion.report.DailyReport
import pl.elpassion.report.edit.ReportEdit

class ReportEditDailyController(val view: ReportEdit.Daily.View) {

    fun onCreate(report: DailyReport) {
        view.showDate(report.date)
    }

}