package pl.elpassion.project.dto

import pl.elpassion.report.Report
import pl.elpassion.report.ReportType

fun newReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportedHours: Double = 4.0, projectId: Long = 1, projectName: String = "Project", description: String = "description", reportType: ReportType = ReportType.REGULAR) =
        Report(id = id, year = year, month = month, day = day, reportedHours = reportedHours, projectId = projectId, projectName = projectName, description = description, reportType = reportType)