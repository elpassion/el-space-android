package pl.elpassion.elspace.debate.details

import io.reactivex.Observable
import pl.elpassion.elspace.api.HubRetrofitProvider
import pl.elpassion.elspace.common.Provider

interface DebateDetails {
    interface Api {
        fun getDebateDetails(token: String): Observable<DebateData>
        fun vote(token: String, answer: Answer): Observable<Unit>
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
        HubRetrofitProvider.get().create(DebateDetails.Api::class.java)
    })
}