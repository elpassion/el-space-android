package pl.elpassion.elspace.hub.report

import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.ReportType.*

enum class ReportType(val id: Int) {
    REGULAR(0),
    PAID_VACATIONS(1),
    UNPAID_VACATIONS(2),
    SICK_LEAVE(3),
    PAID_CONFERENCE(4),
}

fun Int.toReportType() = when (this) {
    R.id.action_regular_report -> REGULAR
    R.id.action_paid_vacations_report -> PAID_VACATIONS
    R.id.action_unpaid_vacations_report -> UNPAID_VACATIONS
    R.id.action_sick_leave_report -> SICK_LEAVE
    R.id.action_paid_conference_report -> PAID_CONFERENCE
    else -> throw IllegalArgumentException()
}

fun ReportType.toActionId() = when (this) {
    REGULAR -> R.id.action_regular_report
    PAID_VACATIONS -> R.id.action_paid_vacations_report
    UNPAID_VACATIONS -> R.id.action_unpaid_vacations_report
    SICK_LEAVE -> R.id.action_sick_leave_report
    PAID_CONFERENCE -> R.id.action_paid_conference_report
}