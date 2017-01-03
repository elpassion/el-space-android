package pl.elpassion.report.add

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Completable

interface ReportAdd {
    interface View {
        fun close()
        fun showError(ex: Throwable)
        fun showDate(date: String)
        fun enableAddReportButton()
        fun showLoader()
        fun hideLoader()
        fun showRegularReportDetails()
        fun showPaidVacationsReportDetails()
        fun showSickLeaveReportDetails()
        fun showUnpaidVacationsReportDetails()
    }

    interface Api {

        @POST("activities")
        fun addReport(
                @Query("activity[performed_at]") date: String,
                @Query("activity[project_id]") projectId: Long,
                @Query("activity[value]") hours: String,
                @Query("activity[comment]") description: String): Completable
    }

    object ApiProvider : Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })
}