package pl.elpassion.elspace.debate.comment

import io.reactivex.Completable
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DebateComment {

    interface Api {
        @FormUrlEncoded
        @POST("comment")
        fun comment(
                @Header("Authorization") token: String,
                @Field("text") message: String,
                @Field("firstName") firstName: String,
                @Field("lastName") lastName: String): Completable
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
    }

    object ApiProvider : Provider<DebateComment.Api>({
        DebateRetrofitProvider.get().create(DebateComment.Api::class.java)
    })
}
