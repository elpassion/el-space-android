package pl.elpassion.report.list

import rx.Observable

interface ReportList {

    interface Api {
        fun getReports(): Observable<List<Report>>
    }

    interface View {
        fun showDays(reports: List<Day>)

        fun showError()

        fun showLoader()

        fun hideLoader()

        fun openAddReportScreen(date: String)

        fun showMonthName(monthName: String)
    }

}