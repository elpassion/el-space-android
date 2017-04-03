package pl.elpassion.elspace.hub.report

import pl.elpassion.elspace.hub.project.Project

interface ReportViewModel {
    val selectedDate: String
}

data class RegularViewModel(override val selectedDate: String,
                            val project: Project?,
                            val description: String,
                            val hours: String) : ReportViewModel

data class PaidVacationsViewModel(override val selectedDate: String, val hours: String) : ReportViewModel

data class DailyViewModel(override val selectedDate: String) : ReportViewModel