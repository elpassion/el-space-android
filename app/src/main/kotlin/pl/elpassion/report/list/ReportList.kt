package pl.elpassion.report.list

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.report.Report
import pl.elpassion.report.list.service.ReportFromApi
import pl.elpassion.report.list.service.ReportListService
import retrofit2.http.GET
import rx.Observable

interface ReportList {

    interface Service {
        fun getReports(): Observable<List<Report>>
    }

    interface View {
        fun showDays(days: List<Day>, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener)

        fun showError(ex: Throwable)

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
        RetrofitProvider.get().create(ReportApi::class.java)
    })

    interface ProjectApi {
        @GET("projects?sort=recent")
        fun getProjects(): Observable<List<Project>>
    }

    object ProjectApiProvider : Provider<ProjectApi>({
        RetrofitProvider.get().create(ProjectApi::class.java)
    })
}

