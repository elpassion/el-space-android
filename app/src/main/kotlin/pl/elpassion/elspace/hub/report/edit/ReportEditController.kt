package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.hub.report.Report

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View) {

    fun onCreate() {
        view.showDate(report.date)
    }
}