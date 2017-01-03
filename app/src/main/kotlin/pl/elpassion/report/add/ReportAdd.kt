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
            fun getHours(): String
        }

        interface PaidVacations {
            fun getHours(): String
        }

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

    interface Sender {
        interface Regular {
            fun sendAddReport(description: String, hours: String)
        }

        interface PaidVacations {
            fun sendAddReport(hours: String)
        }
    }

    object ApiProvider : Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })
}