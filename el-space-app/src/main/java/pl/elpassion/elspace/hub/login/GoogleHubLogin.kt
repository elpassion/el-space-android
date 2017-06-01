package pl.elpassion.elspace.hub.login

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface GoogleHubLogin {
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

    interface Api {
        @POST("api_keys")
        fun loginWithGoogle(@Body body: GoogleTokenForHubTokenApi): Single<HubTokenFromApi>
    }

    data class HubGoogleSignInResult(
            val isSuccess: Boolean,
            val googleToken: String?)

    data class HubTokenFromApi(val accessToken: String)

    data class GoogleTokenForHubTokenApi(val idToken: String)
}