package pl.elpassion.project.dto

import pl.elpassion.report.list.Day
import pl.elpassion.report.list.Report

fun newDay(dayNumber: Int, reports: List<Report> = emptyList(), hasPassed: Boolean = false) =
        Day(dayNumber = dayNumber, reports = reports, hasPassed = hasPassed, isWeekendDay = false)

fun newReport(year: Int = 2016, month: Int = 6, day: Int = 1, reportedHours: Double = 4.0, projectId: Long = 1, projectName: String = "Project", description : String = "description") =
        Report(year = year, month = month, day = day, reportedHours = reportedHours, projectId = projectId, projectName = projectName, description = description)