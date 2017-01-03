package pl.elpassion.report.add.details.paidvacations

import pl.elpassion.report.add.details.ReportAddDetails

class ReportAddDetailsPaidVacationsController(private val view: ReportAddDetails.View.PaidVacations,
                                              private val sender: ReportAddDetails.Sender.PaidVacations) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        sender.sendAddReport(view.getHours())
    }
}