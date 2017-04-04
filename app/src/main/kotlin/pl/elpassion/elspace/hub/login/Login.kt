package pl.elpassion.elspace.hub.login

interface Login {
    interface View {
        fun openReportListScreen()
        fun showEmptyLoginError()
        fun openHubWebsite()
        fun showError()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }
}