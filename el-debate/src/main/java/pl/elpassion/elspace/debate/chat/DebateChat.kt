package pl.elpassion.elspace.debate.chat

import io.reactivex.Observable
import io.reactivex.Single
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.debate.chat.service.DebateChatServiceImpl
import pl.elpassion.elspace.debate.chat.service.DebateChatSocketImpl
import retrofit2.http.*

interface DebateChat {

    interface Service {
        fun initialsCommentsObservable(token: String): Single<InitialsComments>
        fun liveCommentsObservable(debateCode: String, userId: Long): Observable<Comment>
        fun sendComment(commentToSend: CommentToSend): Single<Comment>
    }

    interface Api {
        @FormUrlEncoded
        @POST("comments")
        fun comment(
                @Header("Authorization") token: String,
                @Field("text") message: String,
                @Field("first_name") firstName: String,
                @Field("last_name") lastName: String): Single<Comment>

        @GET("comments")
        fun comment(@Header("Authorization") token: String): Single<InitialsComments>
    }

    interface Socket {
        fun commentsObservable(debateCode: String, userId: Long): Observable<Comment>
    }

    interface View {
        fun showLoader()
        fun hideLoader()
        fun showInitialsComments(initialsComments: List<Comment>)
        fun showInitialsCommentsError(exception: Throwable)
        fun showDebateClosedError()
        fun showLiveComment(liveComment: Comment)
        fun showLiveCommentsError(exception: Throwable)
        fun showSendCommentSuccessPending(comment: Comment)
        fun clearSendCommentInput()
        fun showSendCommentError(exception: Throwable)
        fun showInputOverLimitError()
        fun showCredentialsDialog()
        fun showFirstNameError()
        fun showLastNameError()
        fun closeCredentialsDialog()
    }

    object ServiceProvider : Provider<Service>({
        DebateChatServiceImpl(ApiProvider.get(), SocketProvider.get())
    })

    object ApiProvider : Provider<Api>({
        DebateRetrofitProvider.get().create(Api::class.java)
    })

    object SocketProvider : Provider<Socket>({
        DebateChatSocketImpl()
    })
}
