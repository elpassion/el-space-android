package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.api.RetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Completable

interface ReportEdit {

    interface Api {

        @PATCH("activities/{id}")
        fun editReport(@Path("id") id: Long,
                       @Query("activity[performed_at]") date: String,
                       @Query("activity[value]") reportedHour: String,
                       @Query("activity[comment]") description: String,
                       @Query("activity[project_id]") projectId: Long?): Completable

        @DELETE("activities/{id}")
        fun removeReport(@Path("id") reportId: Long): Completable
    }

    object ApiProvider : Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })
}