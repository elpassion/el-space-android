package pl.elpassion.report.add

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
import pl.elpassion.project.Project
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Completable

interface ReportAdd {
    interface View {
        fun showSelectedProject(project: Project)

        fun openProjectChooser()

        fun close()

        fun showError(ex: Throwable)

        fun showDate(date: String)

        fun enableAddReportButton()

        fun showEmptyDescriptionError()

        fun showLoader()

        fun hideLoader()

        fun hideHoursInput()
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