package pl.elpassion.elspace.hub.login

import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

interface Login {
    interface View {
        fun openReportListScreen()
        fun showEmptyLoginError()
        fun openHubWebsite()
        fun showGoogleTokenError()
        fun showLoader()
        fun hideLoader()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }

    interface HubTokenApi {
        @POST("api_keys")
        fun loginWithGoogleToken(@Body body: GoogleTokenForHubTokenApi): Observable<HubTokenFromApi>
    }
}