package pl.elpassion.elspace.hub.report

import pl.elpassion.R
import pl.elpassion.elspace.hub.report.ReportType.*

enum class ReportType {
    SICK_LEAVE,
    REGULAR,
    PAID_VACATIONS,
    UNPAID_VACATIONS
}

fun Int.toReportType() = when (this) {
    R.id.action_regular_report -> REGULAR
    R.id.action_paid_vacations_report -> PAID_VACATIONS
    R.id.action_sick_leave_report -> SICK_LEAVE
    R.id.action_unpaid_vacations_report -> UNPAID_VACATIONS
    else -> throw IllegalArgumentException()
}

fun ReportType.toActionId() = when (this) {
    REGULAR -> R.id.action_regular_report
    SICK_LEAVE -> R.id.action_sick_leave_report
    PAID_VACATIONS -> R.id.action_paid_vacations_report
    UNPAID_VACATIONS -> R.id.action_unpaid_vacations_report
}