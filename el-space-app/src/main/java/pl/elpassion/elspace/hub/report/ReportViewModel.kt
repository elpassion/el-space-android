package pl.elpassion.elspace.hub.report

import android.support.annotation.IdRes
import pl.elpassion.elspace.R
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

fun getReportViewModel(@IdRes actionId: Int,
                       project: Project?,
                       date: String,
                       hours: String,
                       description: String): ReportViewModel =
        when (actionId) {
            R.id.action_regular_report -> RegularViewModel(date, project, description, hours)
            R.id.action_paid_vacations_report -> PaidVacationsViewModel(date, hours)
            R.id.action_unpaid_vacations_report, R.id.action_sick_leave_report -> DailyViewModel(date)
            else -> throw IllegalArgumentException()
        }