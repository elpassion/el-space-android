package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.hub.report.RegularHourlyReport

class ReportEditController(private val report: RegularHourlyReport,
                           private val view: ReportEdit.View) {

    fun onCreate() {
        view.showDate(report.date)
    }
}