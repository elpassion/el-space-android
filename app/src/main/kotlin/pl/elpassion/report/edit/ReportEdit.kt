package pl.elpassion.report.edit

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
import pl.elpassion.report.RegularHourlyReport
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Completable

interface ReportEdit {
    interface View {
        fun showReport(report: RegularHourlyReport)
        fun openChooseProjectScreen()
        fun updateProjectName(projectName: String)
        fun showLoader()
        fun hideLoader()
        fun showError(ex: Throwable)
        fun close()
        fun showEmptyDescriptionError()
        fun showDate(date: String)
    }

    interface EditApi {
        @PATCH("activities/{id}")
        fun editReport(@Path("id") id: Long,
                       @Query("activity[performed_at]") date: String,
                       @Query("activity[value]") reportedHour: String,
                       @Query("activity[comment]") description: String,
                       @Query("activity[project_id]") projectId: Long?): Completable
    }

    object EditApiProvider : Provider<EditApi>({
        RetrofitProvider.get().create(EditApi::class.java)
    })

    interface RemoveApi {
        @DELETE("activities/{id}")
        fun removeReport(@Path("id") reportId: Long): Completable
    }

    object RemoveApiProvider : Provider<RemoveApi>({
        RetrofitProvider.get().create(RemoveApi::class.java)
    })
}