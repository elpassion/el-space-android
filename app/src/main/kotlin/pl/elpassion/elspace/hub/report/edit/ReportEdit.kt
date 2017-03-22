package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.api.RetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.report.ReportType
import pl.elpassion.elspace.hub.report.ReportViewModel
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Completable
import rx.Observable

interface ReportEdit {

    interface View {
        fun showReportType(type: ReportType)
        fun showDate(date: String)
        fun showReportedHours(reportedHours: Double)
        fun showProjectName(name: String)
        fun showDescription(description: String)
        fun reportTypeChanges(): Observable<ReportType>
        fun showRegularForm()
        fun showPaidVacationsForm()
        fun showSickLeaveForm()
        fun showUnpaidVacationsForm()
        fun editReportClicks(): Observable<ReportViewModel>
        fun close()
        fun showLoader()
    }

    interface Api {

        @PATCH("activities/{id}")
        fun editReport(@Path("id") id: Long,
                       @Query("activity[report_type]") reportType: Int,
                       @Query("activity[performed_at]") date: String,
                       @Query("activity[value]") reportedHour: String?,
                       @Query("activity[comment]") description: String?,
                       @Query("activity[project_id]") projectId: Long?): Completable

        @DELETE("activities/{id}")
        fun removeReport(@Path("id") reportId: Long): Completable
    }

    object ApiProvider : Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })
}