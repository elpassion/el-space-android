package pl.elpassion.login

class LoginController(private val view: Login.View, private val loginRepository: Login.Repository) {

    fun onCreate() {
        if (loginRepository.readToken() != null) {
            addShortcutsIfSupported()
            view.openReportListScreen()
        }
    }

    fun onLogin(token: String) {
        if (token.isNotEmpty()) {
            loginRepository.saveToken(token)
            view.openReportListScreen()
            addShortcutsIfSupported()
        } else {
            view.showEmptyLoginError()
        }
    }

    private fun addShortcutsIfSupported() {
        if (view.hasHandlingShortcuts()) {
            view.creteAppShortcuts()
        }
    }

}