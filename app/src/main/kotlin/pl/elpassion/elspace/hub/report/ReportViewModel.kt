package pl.elpassion.elspace.hub.report

import pl.elpassion.elspace.hub.project.Project

interface ReportViewModel {
    val selectedDate: String
}

data class RegularReport(override val selectedDate: String, val project: Project?, val description: String, val hours: String) : ReportViewModel

data class PaidVacationsReport(override val selectedDate: String, val hours: String) : ReportViewModel

data class UnpaidVacationsReport(override val selectedDate: String) : ReportViewModel

data class SickLeaveReport(override val selectedDate: String) : ReportViewModel
