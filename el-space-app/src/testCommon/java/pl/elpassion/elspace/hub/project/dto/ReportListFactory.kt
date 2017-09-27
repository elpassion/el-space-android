package pl.elpassion.elspace.hub.project.dto

import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.DailyReport
import pl.elpassion.elspace.hub.report.DailyReportType
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.list.DayWithoutReports

fun newRegularHourlyReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportedHours: Double = 4.0, project: Project = newProject(id = 1, name = "Project"), description: String = "description") =
        RegularHourlyReport(id = id, year = year, month = month, day = day, reportedHours = reportedHours, project = project, description = description)

fun newPaidVacationHourlyReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportedHours: Double = 4.0) =
        PaidVacationHourlyReport(id = id, year = year, month = month, day = day, reportedHours = reportedHours)

fun newDailyReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportType: DailyReportType = DailyReportType.SICK_LEAVE) =
        DailyReport(id = id, year = year, month = month, day = day, reportType = reportType)

fun newDayWithoutReports(isWeekend: Boolean = false, hasPassed: Boolean = true) = DayWithoutReports(0, "", "", hasPassed, isWeekend)