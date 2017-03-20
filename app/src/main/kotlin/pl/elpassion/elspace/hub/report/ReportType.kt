package pl.elpassion.elspace.hub.report

import pl.elpassion.R

enum class ReportType {
    SICK_LEAVE,
    REGULAR,
    PAID_VACATIONS,
    UNPAID_VACATIONS
}

fun Int.toReportType() = when (this) {
    R.id.action_regular_report -> ReportType.REGULAR
    R.id.action_paid_vacations_report -> ReportType.PAID_VACATIONS
    R.id.action_sick_leave_report -> ReportType.SICK_LEAVE
    R.id.action_unpaid_vacations_report -> ReportType.UNPAID_VACATIONS
    else -> throw IllegalArgumentException()
}