package pl.elpassion.elspace.hub.login.instant

import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class InstantGoogleHubLoginController(
        val view: InstantGoogleHubLogin.View,
        val repository: InstantGoogleHubLogin.Repository,
        val api: InstantGoogleHubLogin.Api,
        val shortcutService: ShortcutService,
        val schedulers: SchedulersSupplier) {

    fun onCreate() {
        if (repository.readToken() != null) {
            view.openOnLoggedInScreen()
        } else {
            view.startGoogleLoginIntent()
        }
    }

    fun onGoogleSignInResult(hubGoogleSignInResult: InstantGoogleHubLogin.HubGoogleSignInResult) {
        if (hubGoogleSignInResult.isSuccess && hubGoogleSignInResult.googleToken != null) {
            api.loginWithGoogle(hubGoogleSignInResult.googleToken)
                    .subscribeOn(schedulers.backgroundScheduler)
                    .observeOn(schedulers.uiScheduler)
                    .subscribe(this::onSuccess, this::onError)
        } else {
            view.showGoogleLoginError()
        }
    }

    private fun onSuccess(token: String) {
        if (shortcutService.isSupportingShortcuts()) {
            shortcutService.creteAppShortcuts()
        }
        repository.saveToken(token)
        view.openOnLoggedInScreen()
    }

    private fun onError(error: Throwable) {
        view.logoutFromGoogle()
        view.showApiLoginError()
    }
}