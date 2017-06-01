package pl.elpassion.elspace.hub.login.instant

import io.reactivex.Observable
import io.reactivex.Single
import pl.elpassion.elspace.hub.login.GoogleTokenForHubTokenApi
import pl.elpassion.elspace.hub.login.HubTokenFromApi
import retrofit2.http.Body
import retrofit2.http.POST

interface InstantGoogleHubLogin {
    interface View {
        fun openOnLoggedInScreen()
        fun startGoogleLoginIntent()
        fun showGoogleLoginError()
        fun showApiLoginError()
        fun logoutFromGoogle()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }

    data class HubGoogleSignInResult(
            val isSuccess: Boolean,
            val googleToken: String?)

    interface Api {
        @POST("api_keys")
        fun loginWithGoogle(@Body body: GoogleTokenForHubTokenApi): Single<HubTokenFromApi>
    }
}