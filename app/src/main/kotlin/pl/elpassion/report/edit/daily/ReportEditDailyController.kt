package pl.elpassion.report.edit.daily

import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.report.DailyReport
import pl.elpassion.report.edit.ReportEdit

class ReportEditDailyController(val view: ReportEdit.Daily.View) {

    fun onCreate(report: DailyReport) {
        val performedDate = getPerformedAtString(report.year, report.month, report.day)
        view.showDate(performedDate)
    }

}