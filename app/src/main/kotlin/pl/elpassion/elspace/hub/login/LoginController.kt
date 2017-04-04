package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class LoginController(private val view: Login.View,
                      private val loginRepository: Login.Repository,
                      private val shortcutService: ShortcutService,
                      private val api: Login.HubTokenApi) {

    fun onCreate() {
        if (loginRepository.readToken() != null) {
            addShortcutsIfSupported()
            view.openReportListScreen()
        }
    }

    fun onGoogleToken() {
        view.showLoader()
        api.loginWithGoogleToken()
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    onCorrectHubToken(it)
                },{
                    view.showError()
                })
    }

    fun onLogin(token: String) {
        if (token.isNotEmpty()) {
            onCorrectHubToken(token)
        } else {
            view.showEmptyLoginError()
        }
    }

    private fun onCorrectHubToken(token: String) {
        loginRepository.saveToken(token)
        view.openReportListScreen()
        addShortcutsIfSupported()
    }

    private fun addShortcutsIfSupported() {
        if (shortcutService.isSupportingShortcuts()) {
            shortcutService.creteAppShortcuts()
        }
    }

    fun onHub() {
        view.openHubWebsite()
    }

}