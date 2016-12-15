package pl.elpassion.login

interface Login {
    interface View {
        fun openReportListScreen()
        fun showEmptyLoginError()
        fun hasHandlingShortcuts(): Boolean
        fun creteAppShortcuts()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }
}