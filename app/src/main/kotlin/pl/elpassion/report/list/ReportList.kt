package pl.elpassion.report.list

import pl.elpassion.common.Provider
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepositoryProvider
import retrofit2.http.GET
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
        ReportListService(ReportApiProvider.get(), ProjectApiProvider.get(), ProjectRepositoryProvider.get())
    })

    interface ReportApi {
        @GET("activities")
        fun getReports(): Observable<List<ReportFromApi>>
    }

    object ReportApiProvider : Provider<ReportApi>({
        object : ReportApi {
            override fun getReports(): Observable<List<ReportFromApi>> {
                return Observable.error(RuntimeException())
            }
        }
    })

    interface ProjectApi {
        @GET("projects")
        fun getProjects() : Observable<List<Project>>
    }

    object ProjectApiProvider : Provider<ProjectApi>({
        object :ProjectApi{
            override fun getProjects(): Observable<List<Project>> {
                return Observable.error(RuntimeException())
            }
        }
    })
}

