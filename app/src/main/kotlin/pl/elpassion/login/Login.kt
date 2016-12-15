package pl.elpassion.login

interface Login {
    interface View {
        fun openReportListScreen()
        fun showEmptyLoginError()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }
}