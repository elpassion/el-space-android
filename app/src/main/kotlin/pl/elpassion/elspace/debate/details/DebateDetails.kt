package pl.elpassion.elspace.debate.details

import pl.elpassion.elspace.api.RetrofitProvider
import pl.elpassion.elspace.common.Provider
import rx.Observable

interface DebateDetails {
    interface Api {
        fun getDebateDetails(token: String): Observable<DebateData>
        fun vote(token: String, answer: Answer): Observable<Unit>
    }

    interface View {
        fun showDebateDetails(token: String, debateDetails: DebateData)
        fun showLoader()
        fun hideLoader()
        fun showDebateDetailsError(exception: Throwable)
        fun showVoteSuccess()
        fun showVoteError(exception: Throwable)
    }

    object ApiProvider: Provider<DebateDetails.Api>({
        RetrofitProvider.get().create(DebateDetails.Api::class.java)
    })
}