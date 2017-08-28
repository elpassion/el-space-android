package pl.elpassion.elspace.debate.chat

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.debate.chat.model.Comment
import pl.elpassion.elspace.debate.chat.model.CommentToSend
import retrofit2.http.*

interface DebateChat {

    interface Service {
        fun commentsObservable(token: String, debateCode: String): Observable<Comment>
        fun sendComment(commentToSend: CommentToSend): Completable
    }

    interface Api {
        @FormUrlEncoded
        @POST("comments")
        fun comment(
                @Header("Authorization") token: String,
                @Field("text") message: String,
                @Field("first_name") firstName: String,
                @Field("last_name") lastName: String): Completable

        @GET("comments")
        fun comment(@Header("Authorization") token: String): Single<List<Comment>>
    }

    interface Socket {
        fun commentsObservable(debateCode: String): Observable<Comment>
    }

    interface View {
        fun showLoader()
        fun hideLoader()
        fun showComment(comment: Comment)
        fun showCommentError(exception: Throwable)
        fun showSocketError()
        fun clearSendCommentInput()
        fun showSendCommentError(exception: Throwable)
        fun closeScreen()
        fun showInvalidInputError()
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
