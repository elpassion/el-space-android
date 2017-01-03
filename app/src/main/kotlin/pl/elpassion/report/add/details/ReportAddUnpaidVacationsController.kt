package pl.elpassion.report.add.details

class ReportAddUnpaidVacationsController(private val api: ReportAddDetails.Sender.UnpaidVacations) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        api.reportUnpaidVacations()
    }
}