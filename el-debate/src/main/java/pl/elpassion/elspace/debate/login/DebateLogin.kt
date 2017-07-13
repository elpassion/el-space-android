package pl.elpassion.elspace.debate.login

import io.reactivex.Single
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DebateLogin {
    interface View {
        fun fillDebateCode(debateCode: String)
        fun fillDebateNickname(nickname: String)
        fun openDebateScreen(authToken: String)
        fun showLoginFailedError(error: Throwable)
        fun showLoader()
        fun hideLoader()
        fun showWrongPinError()
        fun showWrongNicknameError()
        fun showDebateClosedError()
    }

    interface Api {
        @FormUrlEncoded
        @POST("login")
        fun login(@Field("code") code: String, @Field("username") nickname: String): Single<LoginResponse>
        data class LoginResponse(val authToken: String)
    }

    object ApiProvider: Provider<Api>({
        DebateRetrofitProvider.get().create(Api::class.java)
    })

}