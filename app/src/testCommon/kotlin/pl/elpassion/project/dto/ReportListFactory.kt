package pl.elpassion.project.dto

import pl.elpassion.project.Project
import pl.elpassion.report.DailyReport
import pl.elpassion.report.DayReportType
import pl.elpassion.report.HourlyReport
import pl.elpassion.report.HoursReportType

fun newHoursReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportedHours: Double = 4.0, project: Project? = newProject(id = 1, name = "Project"), description: String = "description", reportType: HoursReportType = HoursReportType.REGULAR) =
        HourlyReport(id = id, year = year, month = month, day = day, reportedHours = reportedHours, project = project, description = description, reportType = reportType)

fun newDayReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportType: DayReportType = DayReportType.SICK_LEAVE) =
        DailyReport(id = id, year = year, month = month, day = day, reportType = reportType)