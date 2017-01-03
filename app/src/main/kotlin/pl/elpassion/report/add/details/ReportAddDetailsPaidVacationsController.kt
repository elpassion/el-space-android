package pl.elpassion.report.add.details

import pl.elpassion.report.add.ReportAdd

class ReportAddDetailsPaidVacationsController(private val view: ReportAdd.View.PaidVacations,
                                              private val sender: ReportAdd.Sender.PaidVacations ) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        sender.sendAddReport(view.getHours())
    }
}