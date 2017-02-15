package pl.elpassion.elspace.hub.report.add

import pl.elpassion.elspace.api.RetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.project.Project
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Completable
import rx.Observable

interface ReportAdd {
    interface View {
        fun close()
        fun showError(ex: Throwable)
        fun showDate(date: String)
        fun showLoader()
        fun hideLoader()
        fun addReportClicks(): Observable<ReportViewModel>
        fun showHoursInput()
        fun showProjectChooser()
        fun showDescriptionInput()
        fun hideDescriptionInput()
        fun hideProjectChooser()
        fun hideHoursInput()
        fun reportTypeChanges(): Observable<ReportType>
        fun showUnpaidVacationsInfo()
        fun showSickLeaveInfo()
        fun hideAdditionalInfo()
        fun showSelectedProject(project: Project)
        fun openProjectChooser()
        fun showEmptyDescriptionError()
        fun showEmptyProjectError()
        fun projectClickEvents(): Observable<Unit>
        fun showRegularForm()
        fun showPaidVacationsForm()
        fun showSickLeaveForm()
    }

    interface Api {

        @POST("activities")
        fun addRegularReport(
                @Query("activity[performed_at]") date: String,
                @Query("activity[project_id]") projectId: Long,
                @Query("activity[value]") hours: String,
                @Query("activity[comment]") description: String): Completable

        @POST("activities?activity[report_type]=1")
        fun addPaidVacationsReport(
                @Query("activity[performed_at]") date: String,
                @Query("activity[value]") hours: String): Completable

        @POST("activities?activity[report_type]=2&activity[value]=0")
        fun addUnpaidVacationsReport(@Query("activity[performed_at]") date: String): Completable

        @POST("activities?activity[report_type]=3&activity[value]=0")
        fun addSickLeaveReport(@Query("activity[performed_at]") date: String): Completable
    }

    object ApiProvider : Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })
}