package pl.elpassion.elspace.debate.comment

import io.reactivex.Completable
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider

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

    object ApiProvider : Provider<DebateComment.Api>({
        DebateRetrofitProvider.get().create(DebateComment.Api::class.java)
    })
}
