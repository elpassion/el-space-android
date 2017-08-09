package pl.elpassion.elspace.debate.chat

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.*

interface DebateChat {

    interface Service {
        fun getLatestComments(token: String): Single<List<Comment>>
        fun updateComments(debateCode: String): PublishSubject<Comment>
        fun sendComment(commentToSend: CommentToSend): Completable
    }

    interface Api {
        @FormUrlEncoded
        @POST("comment")
        fun comment(
                @Header("Authorization") token: String,
                @Field("text") message: String,
                @Field("first_name") firstName: String,
                @Field("last_name") lastName: String): Completable

        @GET("comment")
        fun comment(@Header("Authorization") token: String): Single<List<Comment>>
    }

    interface View {
        fun showLoader()
        fun hideLoader()
        fun showComment(comment: Comment)
        fun showCommentError(exception: Throwable)
        fun showSendCommentSuccess()
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
        ServiceImpl(ApiProvider.get())
    })

    object ApiProvider : Provider<Api>({
        DebateRetrofitProvider.get().create(Api::class.java)
    })
}
