package pl.elpassion.elspace.hub.login.instant

import pl.elpassion.elspace.common.SchedulersSupplier

class InstantGoogleHubLoginController(
        val view: InstantGoogleHubLogin.View,
        val repository: InstantGoogleHubLogin.Repository,
        val api: InstantGoogleHubLogin.Api,
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
                    .subscribe({
                        repository.saveToken(it)
                        view.openOnLoggedInScreen()
                    }, {
                        view.showApiLoginError()
                    })
        } else {
            view.showGoogleLoginError()
        }
    }
}