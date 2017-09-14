package pl.elpassion.elspace.hub.report.list

import io.reactivex.Observable
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.HubRetrofitProvider
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.service.ProjectListService
import pl.elpassion.elspace.hub.report.list.service.ProjectListServiceImpl
import pl.elpassion.elspace.hub.report.list.service.ReportFromApi
import pl.elpassion.elspace.hub.report.list.service.ReportListService
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportList {

    interface Service {
        fun getReports(yearMonth: YearMonth): Observable<List<Report>>
    }

    interface View {
        fun showDays(items: List<AdapterItem>)

        fun showError(ex: Throwable)

        fun showLoader()

        fun hideLoader()

        fun openAddReportScreen(date: String)

        fun openEditReportScreen(report: Report)

        fun showMonthName(monthName: String)

        fun scrollToPosition(position: Int)

        fun isDuringPullToRefresh(): Boolean
    }

    interface Actions {
        fun reportsFilter(): Observable<Boolean>
        fun monthChangeToNext(): Observable<Unit>
        fun monthChangeToPrev(): Observable<Unit>
        fun scrollToCurrent(): Observable<Unit>
        fun reportAdd(): Observable<Unit>
        fun refreshingEvents(): Observable<Unit>
        fun snackBarRetry(): Observable<Unit>
        fun resultRefresh(): Observable<Unit>
    }

    object ServiceProvider : Provider<Service>({
        ReportListService(ReportApiProvider.get(), ProjectListServiceProvider.get())
    })

    interface ReportApi {
        @GET("activities")
        fun getReports(
                @Query("start_date") startDate: String,
                @Query("end_date") endDate: String
        ): Observable<List<ReportFromApi>>
    }

    object ReportApiProvider : Provider<ReportApi>({
        HubRetrofitProvider.get().create(ReportApi::class.java)
    })

    interface ProjectApi {
        @GET("projects?sort=recent")
        fun getProjects(): Observable<List<Project>>
    }

    object ProjectApiProvider : Provider<ProjectApi>({
        HubRetrofitProvider.get().create(ProjectApi::class.java)
    })

    object ProjectListServiceProvider : Provider<ProjectListService>({
        ProjectListServiceImpl(ProjectApiProvider.get(), CachedProjectRepositoryProvider.get())
    })

    data class UIState(
            val adapterItems: List<AdapterItem>,
            val showLoader: Boolean
    )

    sealed class Event {
        object OnCreate : Event()
    }
}

