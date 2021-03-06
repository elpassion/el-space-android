package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.login.GoogleHubLogin.*
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class GoogleHubLoginController(
        val view: View,
        val repository: Repository,
        val api: Api,
        val shortcutService: ShortcutService,
        val schedulers: SchedulersSupplier) {

    fun onCreate() {
        if (repository.readToken() != null) {
            view.openOnLoggedInScreen()
        } else {
            view.startGoogleLoginIntent()
        }
    }

    fun onGoogleSignInResult(hubGoogleSignInResult: HubGoogleSignInResult) {
        if (hubGoogleSignInResult.isSuccess && hubGoogleSignInResult.googleToken != null) {
            api.loginWithGoogle(GoogleTokenForHubTokenApi(hubGoogleSignInResult.googleToken))
                    .map { it.accessToken }
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