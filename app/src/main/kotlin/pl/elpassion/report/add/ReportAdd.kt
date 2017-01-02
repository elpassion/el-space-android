package pl.elpassion.report.add

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
import pl.elpassion.project.Project
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Completable

interface ReportAdd {
    interface View {

        interface Regular {
            fun showSelectedProject(project: Project)
            fun openProjectChooser()
            fun getDescription(): String
            fun showEmptyDescriptionError()
        }

        fun close()
        fun showError(ex: Throwable)
        fun showDate(date: String)
        fun enableAddReportButton()
        fun showLoader()
        fun hideLoader()
        fun hideHoursInput()
        fun showHoursInput()
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

    interface Sender {
        fun sendAddReport(description: String)
    }

    object ApiProvider : Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })
}