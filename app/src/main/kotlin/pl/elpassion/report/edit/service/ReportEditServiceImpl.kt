package pl.elpassion.report.edit.service

import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.edit.ReportEdit
import rx.Completable

class ReportEditServiceImpl(private val api: ReportEdit.EditApi) : ReportEdit.Service {
    override fun edit(report: RegularHourlyReport): Completable {
        val date = getPerformedAtString(year = report.year, month = report.month, day = report.day)
        return api.editReport(id = report.id, description = report.description, date = date, projectId = report.project.id, reportedHour = report.reportedHours.toString())
    }

    override fun edit(report: PaidVacationHourlyReport): Completable {
        val date = getPerformedAtString(year = report.year, month = report.month, day = report.day)
        return api.editReport(id = report.id, description = "", date = date, projectId = null, reportedHour = report.reportedHours.toString())
    }
}