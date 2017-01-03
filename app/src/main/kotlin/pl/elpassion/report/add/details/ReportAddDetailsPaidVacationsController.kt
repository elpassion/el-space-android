package pl.elpassion.report.add.details

class ReportAddDetailsPaidVacationsController(private val view: ReportAddDetails.View.PaidVacations,
                                              private val sender: ReportAddDetails.Sender.PaidVacations) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        sender.sendAddReport(view.getHours())
    }
}