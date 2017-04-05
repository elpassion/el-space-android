package pl.elpassion.elspace.hub.login

import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

interface Login {
    interface View {
        fun openReportListScreen()
        fun showEmptyLoginError()
        fun openHubWebsite()
        fun showError()
        fun showLoader()
        fun hideLoader()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }

    interface HubTokenApi {
        @POST("api_keys")
        fun loginWithGoogleToken(@Query("id_token") googleToken: String): Observable<HubTokenFromApi>
    }
}