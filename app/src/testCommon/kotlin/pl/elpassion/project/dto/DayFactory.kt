package pl.elpassion.project.dto

import pl.elpassion.report.list.Day
import pl.elpassion.report.list.Report

fun newDay(dayNumber: Int, reports: List<Report> = emptyList(), hasPassed: Boolean = false) =
        Day(dayNumber = dayNumber, reports = reports, hasPassed = hasPassed)