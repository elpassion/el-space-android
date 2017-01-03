package pl.elpassion.report.edit.service

import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.edit.ReportEdit
import rx.Completable

class ReportEditServiceImpl(private val api: ReportEdit.EditApi) : ReportEdit.Regular.Service, ReportEdit.PaidVacation.Service {

    override fun edit(report: RegularHourlyReport): Completable = with(report) {
        val date = getPerformedAtString(year = year, month = month, day = day)
        api.editReport(id = id, description = description, date = date, projectId = project.id, reportedHour = reportedHours.toString())
    }

    override fun edit(report: PaidVacationHourlyReport): Completable = with(report) {
        val date = getPerformedAtString(year = year, month = month, day = day)
        api.editReport(id = id, description = "", date = date, projectId = null, reportedHour = reportedHours.toString())
    }
}