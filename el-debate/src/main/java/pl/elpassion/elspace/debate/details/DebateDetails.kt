package pl.elpassion.elspace.debate.details

import io.reactivex.Completable
import io.reactivex.Single
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DebateDetails {
    interface Api {
        @GET("debate")
        fun getDebateDetails(@Header("Authorization") token: String): Single<DebateData>

        @POST("vote")
        fun vote(@Header("Authorization") token: String, @Body answer: Answer): Completable
    }

    interface View {
        fun showDebateDetails(debateDetails: DebateData)
        fun showLoader()
        fun hideLoader()
        fun showDebateDetailsError(exception: Throwable)
        fun showVoteLoader(answer: Answer)
        fun hideVoteLoader()
        fun showVoteSuccess()
        fun showVoteError(exception: Throwable)
        fun resetImagesInButtons()
        fun openCommentScreen()
        fun showSlowDownInformation()
    }

    object ApiProvider : Provider<DebateDetails.Api>({
        DebateRetrofitProvider.get().create(DebateDetails.Api::class.java)
    })
}