package pl.elpassion.elspace.debate.comment

import io.reactivex.Completable

interface DebateComment {

    interface Api {
        fun comment(token: String, message: String): Completable
    }

    interface View {
        fun showLoader()
        fun hideLoader()
        fun showSendCommentSuccess()
        fun showSendCommentError(exception: Throwable)
    }
}
