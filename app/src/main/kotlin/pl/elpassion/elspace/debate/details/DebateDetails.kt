package pl.elpassion.elspace.debate.details

import rx.Observable

interface DebateDetails {
    interface Api {
        fun getDebateDetails(token: String): Observable<DebateData>
        fun vote(token: String, answer: Answer): Observable<Unit>
    }

    interface View {
        fun showDebateDetails(debateDetails: Any)
        fun showLoader()
        fun hideLoader()
        fun showDebateDetailsError(exception: Throwable)
        fun showVoteSuccess()
        fun showVoteError(exception: Throwable)
    }
}