package pl.elpassion.report.list

import pl.elpassion.common.Provider
import rx.Observable

interface ReportList {

    interface Service {
        fun getReports(): Observable<List<Report>>
    }

    interface View {
        fun showDays(days: List<Day>, listener: OnDayClickListener)

        fun showError()

        fun showLoader()

        fun hideLoader()

        fun openAddReportScreen(date: String)

        fun showMonthName(monthName: String)

        fun openEditReportScreen(report: Report)
    }

    object ServiceProvider : Provider<Service>({
        ReportListService(ApiProvider.get())
    })

    interface ReportApi {
        fun getReports(): Observable<List<ReportFromApi>>
    }

    object ApiProvider : Provider<ReportApi>({
        object : ReportApi {
            override fun getReports(): Observable<List<ReportFromApi>> {
                return Observable.error(RuntimeException())
            }
        }
    })
}

