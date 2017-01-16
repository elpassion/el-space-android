package pl.elpassion.report.add.details.sickleave

import pl.elpassion.report.add.details.ReportAddDetails

class ReportAddDetailsSickLeaveController(private val api : ReportAddDetails.Sender.SickLeave) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        api.addSickLeaveReport()
    }
}