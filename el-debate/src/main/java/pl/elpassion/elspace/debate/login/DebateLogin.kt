package pl.elpassion.elspace.debate.login

import io.reactivex.Single
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.debate.LoginCredentials
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DebateLogin {
    interface View {
        fun fillDebateCode(debateCode: String)
        fun openDebateScreen(loginCredentials: LoginCredentials)
        fun showDebateClosedError()
        fun showLoginError(error: Throwable)
        fun showLoader()
        fun hideLoader()
        fun showWrongPinError()
    }

    interface Api {
        @FormUrlEncoded
        @POST("login")
        fun login(@Field("code") code: String): Single<LoginCredentials>
    }

    object ApiProvider : Provider<Api>({
        DebateRetrofitProvider.get().create(Api::class.java)
    })

}