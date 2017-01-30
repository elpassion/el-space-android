package pl.elpassion.elspace.hub.report.edit.service

import pl.elpassion.elspace.hub.report.DailyReport
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import rx.Completable

class ReportEditServiceImpl(private val api: ReportEdit.EditApi) : ReportEdit.Regular.Service, ReportEdit.PaidVacation.Service, ReportEdit.Daily.Service {

    override fun edit(report: RegularHourlyReport): Completable = with(report) {
        api.editReport(id = id, description = description, date = date, projectId = project.id, reportedHour = reportedHours.toString())
    }

    override fun edit(report: PaidVacationHourlyReport): Completable = with(report) {
        api.editReport(id = id, description = "", date = date, projectId = null, reportedHour = reportedHours.toString())
    }

    override fun edit(report: DailyReport): Completable = with(report) {
        api.editReport(id = id, date = date, projectId = null, reportedHour = "0", description = reportType.description)
    }
}