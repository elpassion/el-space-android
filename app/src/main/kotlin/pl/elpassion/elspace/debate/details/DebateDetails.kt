package pl.elpassion.elspace.debate.details

import io.reactivex.Observable
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DebateDetails {
    interface Api {
        @GET("debate")
        fun getDebateDetails(@Header("Authorization") token: String): Observable<DebateData>
        @POST("vote")
        fun vote(@Header("Authorization") token: String, @Body answer: Answer): Observable<Unit>
    }

    interface View {
        fun showDebateDetails(debateDetails: DebateData)
        fun showLoader()
        fun hideLoader()
        fun showDebateDetailsError(exception: Throwable)
        fun showVoteSuccess()
        fun showVoteError(exception: Throwable)
    }

    object ApiProvider: Provider<DebateDetails.Api>({
        DebateRetrofitProvider.get().create(DebateDetails.Api::class.java)
    })
}