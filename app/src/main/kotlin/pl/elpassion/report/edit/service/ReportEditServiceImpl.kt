package pl.elpassion.report.edit.service

import pl.elpassion.report.DailyReport
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.edit.ReportEdit
import rx.Completable

class ReportEditServiceImpl(private val api: ReportEdit.EditApi) : ReportEdit.Regular.Service, ReportEdit.PaidVacation.Service, ReportEdit.Daily.Service {

    override fun edit(report: RegularHourlyReport): Completable = with(report) {
        api.editReport(id = id, description = description, date = date, projectId = project.id, reportedHour = reportedHours.toString())
    }

    override fun edit(report: PaidVacationHourlyReport): Completable = with(report) {
        api.editReport(id = id, description = "", date = date, projectId = null, reportedHour = reportedHours.toString())
    }

    override fun edit(report: DailyReport): Completable = with(report) {
        api.editReport(id = id, date = date, projectId = null, reportedHour = "0", description = "SickLeave")
    }
}