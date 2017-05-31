package pl.elpassion.elspace.hub.report.add

import pl.elpassion.elspace.hub.HubRetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.ReportType
import pl.elpassion.elspace.hub.report.ReportViewModel
import retrofit2.http.POST
import retrofit2.http.Query
import io.reactivex.Observable

interface ReportAdd {
    interface View {
        fun close()
        fun showError(ex: Throwable)
        fun showDate(date: String)
        fun showLoader()
        fun hideLoader()
        fun addReportClicks(): Observable<ReportViewModel>
        fun reportTypeChanges(): Observable<ReportType>
        fun showSelectedProject(project: Project)
        fun openProjectChooser()
        fun showEmptyDescriptionError()
        fun showEmptyProjectError()
        fun projectClickEvents(): Observable<Unit>
        fun showRegularForm()
        fun showPaidVacationsForm()
        fun showSickLeaveForm()
        fun showUnpaidVacationsForm()
    }

    interface Api {

        @POST("activities")
        fun addRegularReport(
                @Query("activity[performed_at]") date: String,
                @Query("activity[project_id]") projectId: Long,
                @Query("activity[value]") hours: String,
                @Query("activity[comment]") description: String): Observable<Unit>

        @POST("activities?activity[report_type]=1")
        fun addPaidVacationsReport(
                @Query("activity[performed_at]") date: String,
                @Query("activity[value]") hours: String): Observable<Unit>

        @POST("activities?activity[report_type]=2&activity[value]=0")
        fun addUnpaidVacationsReport(@Query("activity[performed_at]") date: String): Observable<Unit>

        @POST("activities?activity[report_type]=3&activity[value]=0")
        fun addSickLeaveReport(@Query("activity[performed_at]") date: String): Observable<Unit>
    }

    object ApiProvider : Provider<Api>({
        HubRetrofitProvider.get().create(Api::class.java)
    })
}