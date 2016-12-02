package pl.elpassion.report.edit

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
import pl.elpassion.report.Report
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Completable

interface ReportEdit {
    interface View {
        fun showReport(report: Report)
        fun openChooseProjectScreen()
        fun updateProjectName(projectName: String)
        fun showLoader()
        fun hideLoader()
        fun showError(ex: Throwable)
        fun close()
    }

    interface EditApi {
        @PATCH("activities/{id}")
        fun editReport(@Path("id") id: Long,
                       @Query("activity[performed_at]") date: String,
                       @Query("activity[value]") reportedHour: String,
                       @Query("activity[comment]") description: String,
                       @Query("activity[project_id]") projectId: String): Completable

        fun removeReport(): Completable
    }

    object EditApiProvider : Provider<EditApi>({
        RetrofitProvider.get().create(EditApi::class.java)
    })
}