package pl.elpassion.report.add

import pl.elpassion.common.Provider
import pl.elpassion.project.common.Project
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

interface ReportAdd {
    interface View {
        fun showSelectedProject(project: Project)

        fun openProjectChooser()

        fun close()

        fun showError()

        fun showDate(date: String)
    }

    interface Api {

        @POST("api/v1/activities")
        fun addReport(
                @Query("date") date: String,
                @Query("project_id") projectId: String,
                @Query("value") hours: String,
                @Query("comment") description: String): Observable<Unit>
    }

    object ApiProvider : Provider<Api>({
        throw NotImplementedError()
    })
}