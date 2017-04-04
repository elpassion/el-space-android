package pl.elpassion.elspace.hub.login

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
        fun loginWithGoogleToken(): Observable<String>
    }
}