package pl.elpassion.report.add.details

class ReportAddDetailsUnpaidVacationsController(private val api: ReportAddDetails.Sender.UnpaidVacations) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        api.reportUnpaidVacations()
    }
}