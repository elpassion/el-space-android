package pl.elpassion.report.add.details.unpaidvacations

import pl.elpassion.report.add.details.ReportAddDetails

class ReportAddDetailsUnpaidVacationsController(private val api: ReportAddDetails.Sender.UnpaidVacations) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        api.addUnpaidVacationsReport()
    }
}