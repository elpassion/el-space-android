package pl.elpassion.report.add

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
import pl.elpassion.project.Project
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

interface ReportAdd {
    interface View {
        fun showSelectedProject(project: Project)

        fun openProjectChooser()

        fun close()

        fun showError(it: Throwable)

        fun showDate(date: String)
    }

    interface Api {

        @POST("activities")
        fun addReport(
                @Query("activity[performed_at]") date: String,
                @Query("activity[project_id]") projectId: String,
                @Query("activity[value]") hours: String,
                @Query("activity[comment]") description: String): Observable<Unit>
    }

    object ApiProvider : Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })
}