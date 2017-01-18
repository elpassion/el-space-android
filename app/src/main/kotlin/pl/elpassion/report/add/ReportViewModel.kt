package pl.elpassion.report.add

interface ReportViewModel{
    val selectedDate: String
}

data class RegularReport(override val selectedDate: String) : ReportViewModel

data class PaidVacationsReport(override val selectedDate: String) : ReportViewModel

data class UnpaidVacationsReport(override val selectedDate: String) : ReportViewModel

data class SickLeaveReport(override val selectedDate: String) : ReportViewModel
