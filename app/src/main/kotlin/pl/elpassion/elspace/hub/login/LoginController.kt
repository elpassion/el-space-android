package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class LoginController(private val view: Login.View,
                      private val loginRepository: Login.Repository,
                      private val shortcutService: ShortcutService) {

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
        if (shortcutService.isSupportingShortcuts()) {
            shortcutService.creteAppShortcuts()
        }
    }

    fun onHub() {
        view.openHubWebsite()
    }

    fun onGoogleToken() {
        view.openReportListScreen()
        view.showError()
    }

}