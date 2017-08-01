package pl.elpassion.elspace.debate.chat

import io.reactivex.Completable
import io.reactivex.Observable
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DebateChat {

    interface Service {
        fun comment(comment: Comment): Completable

        fun getComments(token: String): Observable<List<GetComment>>
    }

    interface Api {
        @FormUrlEncoded
        @POST("comment")
        fun comment(
                @Header("Authorization") token: String,
                @Field("text") message: String,
                @Field("first_name") firstName: String,
                @Field("last_name") lastName: String): Completable
    }

    interface View {
        fun showLoader()
        fun hideLoader()
        fun showSendCommentError(exception: Throwable)
        fun closeScreen()
        fun showInvalidInputError()
        fun showInputOverLimitError()
        fun showCredentialsDialog()
        fun showFirstNameError()
        fun showLastNameError()
        fun closeCredentialsDialog()
        fun showComments(commentsList: List<GetComment>)
        fun showCommentsClosed()
    }

    object ServiceProvider : Provider<Service>({
        ServiceImpl(ApiProvider.get())
    })

    object ApiProvider : Provider<Api>({
        DebateRetrofitProvider.get().create(Api::class.java)
    })
}
