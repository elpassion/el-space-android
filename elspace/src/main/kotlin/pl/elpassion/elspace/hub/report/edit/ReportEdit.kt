package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.api.HubRetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.report.ReportType
import pl.elpassion.elspace.hub.report.ReportViewModel
import pl.elpassion.elspace.hub.report.list.service.ReportFromApi
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import io.reactivex.Observable

interface ReportEdit {

    interface View {
        fun showReportType(type: ReportType)
        fun showDate(date: String)
        fun showReportedHours(reportedHours: Double)
        fun showProject(project: Project)
        fun showDescription(description: String)
        fun reportTypeChanges(): Observable<ReportType>
        fun showRegularForm()
        fun showPaidVacationsForm()
        fun showSickLeaveForm()
        fun showUnpaidVacationsForm()
        fun editReportClicks(): Observable<ReportViewModel>
        fun removeReportClicks(): Observable<Unit>
        fun close()
        fun showLoader()
        fun hideLoader()
        fun showError(ex: Throwable)
        fun showEmptyProjectError()
        fun showEmptyDescriptionError()
    }

    interface Api {

        @PATCH("activities/{id}")
        fun editReport(@Path("id") id: Long,
                       @Query("activity[report_type]") reportType: Int,
                       @Query("activity[performed_at]") date: String,
                       @Query("activity[value]") reportedHour: String?,
                       @Query("activity[comment]") description: String?,
                       @Query("activity[project_id]") projectId: Long?): Observable<Unit>

        @DELETE("activities/{id}")
        fun removeReport(@Path("id") reportId: Long): Observable<List<ReportFromApi>>
    }

    object ApiProvider : Provider<Api>({
        HubRetrofitProvider.get().create(Api::class.java)
    })
}