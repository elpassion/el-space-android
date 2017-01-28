package pl.elpassion.login

interface Login {
    interface View {
        fun openReportListScreen()
        fun showEmptyLoginError()
        fun openHubWebsite()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }
}