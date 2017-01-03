package pl.elpassion.report.add.details

class ReportAddDetailsSickLeaveController(private val api : ReportAddDetails.Sender.SickLeave) : ReportAddDetails.Controller {
    override fun onReportAdded() {
        api.reportSickLeave()
    }
}