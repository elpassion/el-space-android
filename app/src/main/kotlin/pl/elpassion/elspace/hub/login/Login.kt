package pl.elpassion.elspace.hub.login

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
        fun loginWithGoogleToken(): Boolean
    }
}