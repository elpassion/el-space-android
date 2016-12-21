package pl.elpassion.report.list

import pl.elpassion.report.Report

data class Day(
        val name: String,
        val date: String,
        val reports: List<Report>,
        val hasPassed: Boolean,
        val reportedHours: Double = reports.sumByDouble { it.reportedHours },
        val isWeekendDay: Boolean)

fun Day.isNotFilledIn(): Boolean = this.hasPassed && this.reports.isEmpty()