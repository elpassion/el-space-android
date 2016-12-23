package pl.elpassion.report.list

import pl.elpassion.report.Report

data class Day(val uuid: Long,
               val name: String,
               val date: String,
               val reports: List<Report>,
               val hasPassed: Boolean,
               val reportedHours: Double = reports.sumByDouble { it.reportedHours },
               val isWeekendDay: Boolean)

fun createDayUUid(year: Int, monthIndex: Int, dayNumber: Int) = (((year + 1) * 10000) + ((monthIndex + 1) * 100) + dayNumber).toLong()

fun Day.isNotFilledIn(): Boolean = this.hasPassed && this.reports.isEmpty()