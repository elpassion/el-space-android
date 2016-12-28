package pl.elpassion.report.edit.service

import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.edit.ReportEdit
import rx.Completable

class ReportEditServiceImpl(private val api: ReportEdit.EditApi) : ReportEdit.Regular.Service, ReportEdit.PaidVacation.Service {
    override fun edit(report: RegularHourlyReport): Completable {
        val date = getPerformedAtString(year = report.year, month = report.month, day = report.day)
        return api.editReport(id = report.id, description = report.description, date = date, projectId = report.project.id, reportedHour = report.reportedHours.toString())
    }

    override fun edit(id: Long, date: String, reportedHours: Double): Completable {
        return api.editReport(id = id, description = "", date = date, projectId = null, reportedHour = reportedHours.toString())
    }
}